package com.sylvia.clothes.service;

import com.sylvia.clothes.dto.BackupDTO;
import com.sylvia.clothes.dto.ClothesDTO;
import com.sylvia.clothes.dto.StatisticsDTO;
import com.sylvia.clothes.entity.Clothes;
import com.sylvia.clothes.jpa.ClothesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * ClothesService
 * </pre>
 *
 * @author Eden
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ClothesService {

  private final ClothesRepository clothesRepository;

  /**
   * 新增衣物
   */
  @Transactional
  public ClothesDTO addClothes(ClothesDTO dto) {
    log.info("新增衣物: {}", dto.getName());

    Clothes clothes = Clothes.builder()
        .name(dto.getName())
        .brand(dto.getBrand())
        .category(dto.getCategory())
        .season(dto.getSeason())
        .color(dto.getColor())
        .style(dto.getStyle())
        .price(dto.getPrice())
        .image(dto.getImage())
        .note(dto.getNote())
        .build();

    Clothes saved = clothesRepository.save(clothes);
    return convertToDTO(saved);
  }

  /**
   * 編輯衣物
   */
  @Transactional
  public ClothesDTO updateClothes(String id, ClothesDTO dto) {
    log.info("編輯衣物: {}", id);

    Clothes clothes = clothesRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("衣物不存在"));

    clothes.setName(dto.getName());
    clothes.setBrand(dto.getBrand());
    clothes.setCategory(dto.getCategory());
    clothes.setSeason(dto.getSeason());
    clothes.setColor(dto.getColor());
    clothes.setStyle(dto.getStyle());
    clothes.setPrice(dto.getPrice());
    if (dto.getImage() != null) {
      clothes.setImage(dto.getImage());
    }
    clothes.setNote(dto.getNote());

    Clothes updated = clothesRepository.save(clothes);
    return convertToDTO(updated);
  }

  /**
   * 刪除衣物
   */
  @Transactional
  public void deleteClothes(String id) {
    log.info("刪除衣物: {}", id);
    clothesRepository.deleteById(id);
  }

  /**
   * 獲取單個衣物
   */
  public ClothesDTO getClothes(String id) {
    Clothes clothes = clothesRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("衣物不存在"));
    return convertToDTO(clothes);
  }

  /**
   * 獲取所有衣物
   */
  public List<ClothesDTO> getAllClothes() {
    return clothesRepository.findAll()
        .stream()
        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * 按類別篩選
   */
  public List<ClothesDTO> getByCategory(String category) {
    return clothesRepository.findByCategory(category)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * 按季節篩選
   */
  public List<ClothesDTO> getBySeason(String season) {
    return clothesRepository.findBySeason(season)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * 搜尋衣物
   */
  public List<ClothesDTO> searchClothes(String keyword) {
    return clothesRepository.searchByKeyword(keyword)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * 複合篩選和搜尋
   */
  public List<ClothesDTO> filterClothes(String category, String season, String keyword) {
    return clothesRepository.filterClothes(category, season, keyword)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * 獲取統計信息
   */
  public StatisticsDTO getStatistics() {
    List<Clothes> allClothes = clothesRepository.findAll();
    List<Object[]> categoryStats = clothesRepository.countByCategory();

    return StatisticsDTO.builder()
        .totalClothes(allClothes.size())
        .totalCategories(categoryStats.size())
        .categoryBreakdown(categoryStats.stream()
            .collect(Collectors.toMap(
                stat -> (String) stat[0],
                stat -> ((Number) stat[1]).intValue()
            )))
        .build();
  }

  /**
   * 導出備份
   */
  public BackupDTO exportBackup() {
    log.info("導出備份");
    List<ClothesDTO> clothesList = getAllClothes();
    return new BackupDTO(clothesList);
  }

  /**
   * 導入備份
   */
  @Transactional
  public BackupDTO importBackup(BackupDTO backup) {
    log.info("導入備份，共 {} 件衣物", backup.getClothes().size());

    backup.getClothes().forEach(dto -> {
      Clothes clothes = Clothes.builder()
          .name(dto.getName())
          .brand(dto.getBrand())
          .category(dto.getCategory())
          .season(dto.getSeason())
          .color(dto.getColor())
          .style(dto.getStyle())
          .price(dto.getPrice())
          .image(dto.getImage())
          .note(dto.getNote())
          .build();
      clothesRepository.save(clothes);
    });

    return exportBackup();
  }

  /**
   * 批次導入
   */
  @Transactional
  public List<ClothesDTO> batchImport(List<ClothesDTO> clothesList) {
    log.info("批次導入，共 {} 件衣物", clothesList.size());

    return clothesList.stream()
        .map(this::addClothes)
        .collect(Collectors.toList());
  }

  /**
   * 轉換為 DTO
   */
  private ClothesDTO convertToDTO(Clothes clothes) {
    return ClothesDTO.builder()
        .id(clothes.getId())
        .name(clothes.getName())
        .brand(clothes.getBrand())
        .category(clothes.getCategory())
        .season(clothes.getSeason())
        .color(clothes.getColor())
        .style(clothes.getStyle())
        .price(clothes.getPrice())
        .image(clothes.getImage())
        .note(clothes.getNote())
        .createdAt(clothes.getCreatedAt())
        .updatedAt(clothes.getUpdatedAt())
        .build();
  }
}
