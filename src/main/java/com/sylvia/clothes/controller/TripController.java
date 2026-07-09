package com.sylvia.clothes.controller;

import com.sylvia.clothes.dto.TripBackupDTO;
import com.sylvia.clothes.dto.TripDTO;
import com.sylvia.clothes.dto.TripDayClothesUpdateDTO;
import com.sylvia.clothes.dto.TripDayDTO;
import com.sylvia.clothes.dto.TripDayNoteUpdateDTO;
import com.sylvia.clothes.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * TripController - 旅行（第一層：旅行本身 / 第二層：每日安排＋衣物）
 * </pre>
 *
 * @author Eden
 */
@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TripController {

  private final TripService tripService;

  /**
   * 新增旅行（第一層：目的地、出發日期、回程日期、行程描述）
   * 建立後會自動依日期區間展開每一天
   * POST /api/trips
   */
  @PostMapping
  public ResponseEntity<TripDTO> createTrip(@Valid @RequestBody TripDTO dto) {
    log.info("POST /trips - 新增旅行");
    TripDTO result = tripService.createTrip(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  /**
   * 編輯旅行
   * PUT /api/trips/{id}
   */
  @PutMapping("/{id}")
  public ResponseEntity<TripDTO> updateTrip(
      @PathVariable String id,
      @Valid @RequestBody TripDTO dto) {
    log.info("PUT /trips/{} - 編輯旅行", id);
    TripDTO result = tripService.updateTrip(id, dto);
    return ResponseEntity.ok(result);
  }

  /**
   * 刪除旅行
   * DELETE /api/trips/{id}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deleteTrip(@PathVariable String id) {
    log.info("DELETE /trips/{} - 刪除旅行", id);
    tripService.deleteTrip(id);
    Map<String, String> response = new HashMap<>();
    response.put("message", "旅行已刪除");
    return ResponseEntity.ok(response);
  }

  /**
   * 取得所有旅行（列表）
   * GET /api/trips
   */
  @GetMapping
  public ResponseEntity<List<TripDTO>> getAllTrips() {
    log.info("GET /trips - 取得所有旅行");
    List<TripDTO> result = tripService.getAllTrips();
    return ResponseEntity.ok(result);
  }

  /**
   * 取得單一旅行詳情（含每日資料，第二層）
   * GET /api/trips/{id}
   */
  @GetMapping("/{id}")
  public ResponseEntity<TripDTO> getTripDetail(@PathVariable String id) {
    log.info("GET /trips/{} - 取得旅行詳情", id);
    TripDTO result = tripService.getTripDetail(id);
    return ResponseEntity.ok(result);
  }

  /**
   * 更新某一天的備註
   * PATCH /api/trips/{tripId}/days/{dayId}/note
   */
  @PatchMapping("/{tripId}/days/{dayId}/note")
  public ResponseEntity<TripDayDTO> updateDayNote(
      @PathVariable String tripId,
      @PathVariable String dayId,
      @RequestBody TripDayNoteUpdateDTO dto) {
    log.info("PATCH /trips/{}/days/{}/note - 更新備註", tripId, dayId);
    TripDayDTO result = tripService.updateDayNote(tripId, dayId, dto.getNote());
    return ResponseEntity.ok(result);
  }

  /**
   * 新增衣物到某一天（可一次多件：上衣、褲子...等分類皆可複選）
   * POST /api/trips/{tripId}/days/{dayId}/clothes
   */
  @PostMapping("/{tripId}/days/{dayId}/clothes")
  public ResponseEntity<TripDayDTO> addClothesToDay(
      @PathVariable String tripId,
      @PathVariable String dayId,
      @RequestBody TripDayClothesUpdateDTO dto) {
    log.info("POST /trips/{}/days/{}/clothes - 新增衣物", tripId, dayId);
    TripDayDTO result = tripService.addClothesToDay(tripId, dayId, dto.getClothesIds());
    return ResponseEntity.ok(result);
  }

  /**
   * 從某一天移除一件衣物
   * DELETE /api/trips/{tripId}/days/{dayId}/clothes/{clothesId}
   */
  @DeleteMapping("/{tripId}/days/{dayId}/clothes/{clothesId}")
  public ResponseEntity<TripDayDTO> removeClothesFromDay(
      @PathVariable String tripId,
      @PathVariable String dayId,
      @PathVariable String clothesId) {
    log.info("DELETE /trips/{}/days/{}/clothes/{} - 移除衣物", tripId, dayId, clothesId);
    TripDayDTO result = tripService.removeClothesFromDay(tripId, dayId, clothesId);
    return ResponseEntity.ok(result);
  }

  /**
   * 導出旅行備份
   * GET /api/trips/backup/export
   */
  @GetMapping("/backup/export")
  public ResponseEntity<TripBackupDTO> exportBackup() {
    log.info("GET /trips/backup/export - 導出旅行備份");
    TripBackupDTO result = tripService.exportBackup();
    return ResponseEntity.ok(result);
  }

  /**
   * 導入旅行備份
   * POST /api/trips/backup/import
   */
  @PostMapping("/backup/import")
  public ResponseEntity<Map<String, Object>> importBackup(
      @RequestBody TripBackupDTO backup) {
    log.info("POST /trips/backup/import - 導入旅行備份");
    TripBackupDTO result = tripService.importBackup(backup);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "旅行備份導入成功");
    response.put("count", result.getTrips().size());
    response.put("backupDate", result.getBackupDate());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
