package com.sylvia.clothes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * BatchImportResponseDTO
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchImportResponseDTO {

  private String message;

  private int count;

  private List<ClothesDTO> data;
}
