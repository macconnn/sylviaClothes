package com.sylvia.clothes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * TripDayClothesUpdateDTO - 新增衣物到某一天（可一次多件，因為上衣/褲子/裙子等可複選）
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDayClothesUpdateDTO {

  private List<String> clothesIds;
}
