/**
 * EZTRAVEL CONFIDENTIAL
 *
 * @Package: com.sylvia.clothes.controller
 * @FileName: ClothesController.java
 * @author: Eden
 * @date: 2026/7/8, 下午 02:02
 *
 * <pre>
 *  Copyright 2026 The ezTravel Co., Ltd. all rights reserved.
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of ezTravel Co., Ltd. and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to ezTravel Co., Ltd. and its suppliers
 *  and may be covered by TAIWAN and Foreign Patents, patents in
 *  process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from ezTravel Co., Ltd.
 *  </pre>
 */

package com.sylvia.clothes.controller;

import com.sylvia.clothes.dto.BackupDTO;
import com.sylvia.clothes.dto.ClothesDTO;
import com.sylvia.clothes.dto.StatisticsDTO;
import com.sylvia.clothes.service.ClothesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * ClothesController
 * </pre>
 *
 * @author Eden
 */

@RestController
@RequestMapping("/clothes")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ClothesController {

  private final ClothesService clothesService;

  /**
   * 新增衣物
   * POST /api/clothes
   */
  @PostMapping
  public ResponseEntity<ClothesDTO> addClothes(@Valid @RequestBody ClothesDTO dto) {
    log.info("POST /clothes - 新增衣物");
    ClothesDTO result = clothesService.addClothes(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  /**
   * 編輯衣物
   * PUT /api/clothes/{id}
   */
  @PutMapping("/{id}")
  public ResponseEntity<ClothesDTO> updateClothes(
      @PathVariable String id,
      @Valid @RequestBody ClothesDTO dto) {
    log.info("PUT /clothes/{} - 編輯衣物", id);
    ClothesDTO result = clothesService.updateClothes(id, dto);
    return ResponseEntity.ok(result);
  }

  /**
   * 刪除衣物
   * DELETE /api/clothes/{id}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deleteClothes(@PathVariable String id) {
    log.info("DELETE /clothes/{} - 刪除衣物", id);
    clothesService.deleteClothes(id);
    Map<String, String> response = new HashMap<>();
    response.put("message", "衣物已刪除");
    return ResponseEntity.ok(response);
  }

  /**
   * 獲取單個衣物
   * GET /api/clothes/{id}
   */
  @GetMapping("/{id}")
  public ResponseEntity<ClothesDTO> getClothes(@PathVariable String id) {
    log.info("GET /clothes/{} - 獲取衣物詳情", id);
    ClothesDTO result = clothesService.getClothes(id);
    return ResponseEntity.ok(result);
  }

  /**
   * 獲取所有衣物
   * GET /api/clothes
   */
  @GetMapping
  public ResponseEntity<List<ClothesDTO>> getAllClothes() {
    log.info("GET /clothes - 獲取所有衣物");
    List<ClothesDTO> result = clothesService.getAllClothes();
    return ResponseEntity.ok(result);
  }

  /**
   * 按類別篩選
   * GET /api/clothes/filter/category?category=上衣
   */
  @GetMapping("/filter/category")
  public ResponseEntity<List<ClothesDTO>> getByCategory(
      @RequestParam String category) {
    log.info("GET /clothes/filter/category - 按類別篩選: {}", category);
    List<ClothesDTO> result = clothesService.getByCategory(category);
    return ResponseEntity.ok(result);
  }

  /**
   * 按季節篩選
   * GET /api/clothes/filter/season?season=春夏
   */
  @GetMapping("/filter/season")
  public ResponseEntity<List<ClothesDTO>> getBySeason(
      @RequestParam String season) {
    log.info("GET /clothes/filter/season - 按季節篩選: {}", season);
    List<ClothesDTO> result = clothesService.getBySeason(season);
    return ResponseEntity.ok(result);
  }

  /**
   * 搜尋衣物
   * GET /api/clothes/search?keyword=白色
   */
  @GetMapping("/search")
  public ResponseEntity<List<ClothesDTO>> searchClothes(
      @RequestParam String keyword) {
    log.info("GET /clothes/search - 搜尋: {}", keyword);
    List<ClothesDTO> result = clothesService.searchClothes(keyword);
    return ResponseEntity.ok(result);
  }

  /**
   * 複合篩選
   * GET /api/clothes/filter?category=上衣&season=春夏&keyword=白色
   */
  @GetMapping("/filter")
  public ResponseEntity<List<ClothesDTO>> filterClothes(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String season,
      @RequestParam(required = false) String keyword) {
    log.info("GET /clothes/filter - 複合篩選: category={}, season={}, keyword={}",
        category, season, keyword);
    List<ClothesDTO> result = clothesService.filterClothes(category, season, keyword);
    return ResponseEntity.ok(result);
  }

  /**
   * 獲取統計信息
   * GET /api/clothes/statistics
   */
  @GetMapping("/statistics")
  public ResponseEntity<StatisticsDTO> getStatistics() {
    log.info("GET /clothes/statistics - 獲取統計信息");
    StatisticsDTO result = clothesService.getStatistics();
    return ResponseEntity.ok(result);
  }

  /**
   * 批次導入
   * POST /api/clothes/batch-import
   */
  @PostMapping("/batch-import")
  public ResponseEntity<Map<String, Object>> batchImport(
      @RequestBody List<ClothesDTO> clothesList) {
    log.info("POST /clothes/batch-import - 批次導入 {} 件衣物", clothesList.size());
    List<ClothesDTO> result = clothesService.batchImport(clothesList);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "批次導入成功");
    response.put("count", result.size());
    response.put("data", result);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 導出備份
   * GET /api/clothes/backup/export
   */
  @GetMapping("/backup/export")
  public ResponseEntity<BackupDTO> exportBackup() {
    log.info("GET /clothes/backup/export - 導出備份");
    BackupDTO result = clothesService.exportBackup();
    return ResponseEntity.ok(result);
  }

  /**
   * 導入備份
   * POST /api/clothes/backup/import
   */
  @PostMapping("/backup/import")
  public ResponseEntity<Map<String, Object>> importBackup(
      @RequestBody BackupDTO backup) {
    log.info("POST /clothes/backup/import - 導入備份");
    BackupDTO result = clothesService.importBackup(backup);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "備份導入成功");
    response.put("count", result.getClothes().size());
    response.put("backupDate", result.getBackupDate());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 健康檢查
   * GET /api/clothes/health
   */
  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> health() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "UP");
    response.put("message", "Wardrobe API is running");
    return ResponseEntity.ok(response);
  }
}
