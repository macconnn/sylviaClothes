package com.sylvia.clothes.service;

import com.sylvia.clothes.dto.ClothesDTO;
import com.sylvia.clothes.dto.TripBackupDTO;
import com.sylvia.clothes.dto.TripDTO;
import com.sylvia.clothes.dto.TripDayDTO;
import com.sylvia.clothes.entity.Clothes;
import com.sylvia.clothes.entity.Trip;
import com.sylvia.clothes.entity.TripDay;
import com.sylvia.clothes.jpa.ClothesRepository;
import com.sylvia.clothes.jpa.TripDayRepository;
import com.sylvia.clothes.jpa.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 * TripService - 旅行（第一層）與旅行日（第二層）的商業邏輯
 * 建立/修改旅行時，會依照出發~回程日期自動展開每一天(TripDay)
 * </pre>
 *
 * @author Eden
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

  private final TripRepository tripRepository;
  private final TripDayRepository tripDayRepository;
  private final ClothesRepository clothesRepository;

  /**
   * 新增旅行：建立 Trip 後，依日期區間自動展開每一天
   */
  @Transactional
  public TripDTO createTrip(TripDTO dto) {
    validateDateRange(dto.getStartDate(), dto.getEndDate());
    log.info("新增旅行: {} ({} ~ {})", dto.getDestination(), dto.getStartDate(), dto.getEndDate());

    Trip trip = Trip.builder()
        .destination(dto.getDestination())
        .startDate(dto.getStartDate())
        .endDate(dto.getEndDate())
        .description(dto.getDescription())
        .build();

    Trip saved = tripRepository.save(trip);

    List<TripDay> days = buildDaysForRange(saved, dto.getStartDate(), dto.getEndDate());
    tripDayRepository.saveAll(days);

    return getTripDetail(saved.getId());
  }

  /**
   * 編輯旅行：目的地/描述直接更新；若日期區間變動，重新展開天數，
   * 已存在且日期仍在新區間內的 TripDay（含備註、已選衣物）會被保留
   */
  @Transactional
  public TripDTO updateTrip(String id, TripDTO dto) {
    validateDateRange(dto.getStartDate(), dto.getEndDate());
    log.info("編輯旅行: {}", id);

    Trip trip = tripRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("旅行不存在"));

    trip.setDestination(dto.getDestination());
    trip.setDescription(dto.getDescription());

    boolean dateChanged = !trip.getStartDate().equals(dto.getStartDate())
        || !trip.getEndDate().equals(dto.getEndDate());

    if (dateChanged) {
      List<TripDay> existingDays = tripDayRepository.findByTripIdOrderByDayIndexAsc(id);
      Map<LocalDate, TripDay> existingByDate = existingDays.stream()
          .collect(Collectors.toMap(TripDay::getDate, d -> d, (a, b) -> a, LinkedHashMap::new));

      List<LocalDate> newDates = datesBetween(dto.getStartDate(), dto.getEndDate());

      List<TripDay> toKeepOrCreate = new ArrayList<>();
      int idx = 1;
      for (LocalDate date : newDates) {
        TripDay day = existingByDate.remove(date);
        if (day == null) {
          day = TripDay.builder()
              .trip(trip)
              .date(date)
              .dayIndex(idx)
              .build();
        } else {
          day.setDayIndex(idx);
        }
        toKeepOrCreate.add(day);
        idx++;
      }

      // 剩下沒被用到的舊日期 -> 刪除（連同 trip_day_clothes 關聯一併移除）
      if (!existingByDate.isEmpty()) {
        tripDayRepository.deleteAll(existingByDate.values());
      }

      trip.setStartDate(dto.getStartDate());
      trip.setEndDate(dto.getEndDate());
      tripRepository.save(trip);
      tripDayRepository.saveAll(toKeepOrCreate);
    } else {
      tripRepository.save(trip);
    }

    return getTripDetail(id);
  }

  /**
   * 刪除旅行（連同底下所有 TripDay 與衣物關聯一併刪除）
   */
  @Transactional
  public void deleteTrip(String id) {
    log.info("刪除旅行: {}", id);
    if (!tripRepository.existsById(id)) {
      throw new RuntimeException("旅行不存在");
    }
    tripRepository.deleteById(id);
  }

  /**
   * 取得所有旅行（列表用，不含每日細節）
   */
  public List<TripDTO> getAllTrips() {
    return tripRepository.findAll().stream()
        .sorted(Comparator.comparing(Trip::getStartDate).reversed())
        .map(this::convertToSummaryDTO)
        .collect(Collectors.toList());
  }

  /**
   * 取得單一旅行詳情（含每日資料與已選衣物）
   */
  public TripDTO getTripDetail(String id) {
    Trip trip = tripRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("旅行不存在"));

    List<TripDayDTO> dayDTOs = tripDayRepository.findByTripIdOrderByDayIndexAsc(id).stream()
        .map(this::convertDayToDTO)
        .collect(Collectors.toList());

    int itemCount = dayDTOs.stream().mapToInt(d -> d.getClothes().size()).sum();

    return TripDTO.builder()
        .id(trip.getId())
        .destination(trip.getDestination())
        .startDate(trip.getStartDate())
        .endDate(trip.getEndDate())
        .description(trip.getDescription())
        .totalDays(dayDTOs.size())
        .itemCount(itemCount)
        .days(dayDTOs)
        .createdAt(trip.getCreatedAt())
        .updatedAt(trip.getUpdatedAt())
        .build();
  }

  /**
   * 更新單一天的備註
   */
  @Transactional
  public TripDayDTO updateDayNote(String tripId, String dayId, String note) {
    TripDay day = getDayOrThrow(tripId, dayId);
    day.setNote(note);
    TripDay saved = tripDayRepository.save(day);
    return convertDayToDTO(saved);
  }

  /**
   * 新增衣物到某一天（可一次多件，重複的會自動略過）
   */
  @Transactional
  public TripDayDTO addClothesToDay(String tripId, String dayId, List<String> clothesIds) {
    TripDay day = getDayOrThrow(tripId, dayId);

    List<Clothes> toAdd = clothesRepository.findAllById(clothesIds);
    for (Clothes c : toAdd) {
      boolean exists = day.getClothesList().stream().anyMatch(existing -> existing.getId().equals(c.getId()));
      if (!exists) {
        day.getClothesList().add(c);
      }
    }

    TripDay saved = tripDayRepository.save(day);
    return convertDayToDTO(saved);
  }

  /**
   * 從某一天移除一件衣物
   */
  @Transactional
  public TripDayDTO removeClothesFromDay(String tripId, String dayId, String clothesId) {
    TripDay day = getDayOrThrow(tripId, dayId);
    day.getClothesList().removeIf(c -> c.getId().equals(clothesId));
    TripDay saved = tripDayRepository.save(day);
    return convertDayToDTO(saved);
  }

  /**
   * 導出旅行備份（含每日安排與已選衣物），用法與 ClothesService.exportBackup() 相同
   */
  public TripBackupDTO exportBackup() {
    log.info("導出旅行備份");
    List<TripDTO> tripList = tripRepository.findAll().stream()
        .map(t -> getTripDetail(t.getId()))
        .collect(Collectors.toList());
    return new TripBackupDTO(tripList);
  }

  /**
   * 導入旅行備份：依備份中的日期區間重新展開每一天，
   * 並還原每天的備註與已選衣物（衣物以 id 對應現有衣櫃資料）
   */
  @Transactional
  public TripBackupDTO importBackup(TripBackupDTO backup) {
    log.info("導入旅行備份，共 {} 趟旅行", backup.getTrips().size());
    backup.getTrips().forEach(this::importSingleTrip);
    return exportBackup();
  }

  private void importSingleTrip(TripDTO dto) {
    Trip trip = Trip.builder()
        .destination(dto.getDestination())
        .startDate(dto.getStartDate())
        .endDate(dto.getEndDate())
        .description(dto.getDescription())
        .build();
    Trip saved = tripRepository.save(trip);

    List<TripDay> days = buildDaysForRange(saved, dto.getStartDate(), dto.getEndDate());
    Map<LocalDate, TripDay> byDate = days.stream()
        .collect(Collectors.toMap(TripDay::getDate, d -> d, (a, b) -> a, LinkedHashMap::new));

    if (dto.getDays() != null) {
      for (TripDayDTO dayDto : dto.getDays()) {
        TripDay day = byDate.get(dayDto.getDate());
        if (day == null) {
          continue;
        }
        day.setNote(dayDto.getNote());
        if (dayDto.getClothes() != null && !dayDto.getClothes().isEmpty()) {
          List<String> ids = dayDto.getClothes().stream()
              .map(ClothesDTO::getId)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
          List<Clothes> found = clothesRepository.findAllById(ids);
          day.setClothesList(new ArrayList<>(found));
        }
      }
    }

    tripDayRepository.saveAll(days);
  }

  // ===== 私有輔助方法 =====

  private TripDay getDayOrThrow(String tripId, String dayId) {
    TripDay day = tripDayRepository.findById(dayId)
        .orElseThrow(() -> new RuntimeException("旅行日不存在"));
    if (!day.getTrip().getId().equals(tripId)) {
      throw new RuntimeException("此日期不屬於該旅行");
    }
    return day;
  }

  private void validateDateRange(LocalDate start, LocalDate end) {
    if (start == null || end == null) {
      throw new RuntimeException("出發日期與回程日期不能為空");
    }
    if (end.isBefore(start)) {
      throw new RuntimeException("回程日期不能早於出發日期");
    }
  }

  private List<LocalDate> datesBetween(LocalDate start, LocalDate end) {
    List<LocalDate> dates = new ArrayList<>();
    LocalDate cur = start;
    while (!cur.isAfter(end)) {
      dates.add(cur);
      cur = cur.plusDays(1);
    }
    return dates;
  }

  private List<TripDay> buildDaysForRange(Trip trip, LocalDate start, LocalDate end) {
    List<TripDay> days = new ArrayList<>();
    int idx = 1;
    for (LocalDate date : datesBetween(start, end)) {
      days.add(TripDay.builder()
          .trip(trip)
          .date(date)
          .dayIndex(idx)
          .build());
      idx++;
    }
    return days;
  }

  private TripDTO convertToSummaryDTO(Trip trip) {
    List<TripDay> days = tripDayRepository.findByTripIdOrderByDayIndexAsc(trip.getId());
    int itemCount = days.stream().mapToInt(d -> d.getClothesList().size()).sum();

    return TripDTO.builder()
        .id(trip.getId())
        .destination(trip.getDestination())
        .startDate(trip.getStartDate())
        .endDate(trip.getEndDate())
        .description(trip.getDescription())
        .totalDays(days.size())
        .itemCount(itemCount)
        .days(null)
        .createdAt(trip.getCreatedAt())
        .updatedAt(trip.getUpdatedAt())
        .build();
  }

  private TripDayDTO convertDayToDTO(TripDay day) {
    List<ClothesDTO> clothesDTOs = day.getClothesList().stream()
        .map(this::convertClothesToDTO)
        .collect(Collectors.toList());

    return TripDayDTO.builder()
        .id(day.getId())
        .tripId(day.getTrip().getId())
        .date(day.getDate())
        .dayIndex(day.getDayIndex())
        .note(day.getNote())
        .clothes(clothesDTOs)
        .build();
  }

  private ClothesDTO convertClothesToDTO(Clothes c) {
    return ClothesDTO.builder()
        .id(c.getId())
        .name(c.getName())
        .brand(c.getBrand())
        .category(c.getCategory())
        .season(c.getSeason())
        .color(c.getColor())
        .style(c.getStyle())
        .price(c.getPrice())
        .image(c.getImage())
        .note(c.getNote())
        .createdAt(c.getCreatedAt())
        .updatedAt(c.getUpdatedAt())
        .build();
  }
}
