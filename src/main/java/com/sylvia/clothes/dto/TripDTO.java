package com.sylvia.clothes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * TripDTO - 旅行（第一層）：目的地、出發/回程日期、行程描述
 * days 只有在「取得單一旅行詳情」時才會帶完整資料，列表 API 為節省流量會是 null
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {

  private String id;

  @NotBlank(message = "目的地不能為空")
  private String destination;

  @NotNull(message = "出發日期不能為空")
  private LocalDate startDate;

  @NotNull(message = "回程日期不能為空")
  private LocalDate endDate;

  private String description;

  private Integer totalDays; // 共幾天

  private Integer itemCount; // 已安排的衣物件數總和（列表用）

  private List<TripDayDTO> days; // 詳情才會有值

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
