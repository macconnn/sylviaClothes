package com.sylvia.clothes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <pre>
 * StatisticsDTO
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
  private int totalClothes;
  private int totalCategories;
  private Map<String, Integer> categoryBreakdown;
}
