package com.sylvia.clothes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * TripDayDTO - 旅行中單一天的資料，含當天已選擇的衣物清單
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDayDTO {

  private String id;

  private String tripId;

  private LocalDate date;

  private int dayIndex; // DAY 幾

  private String note;

  private List<ClothesDTO> clothes;
}
