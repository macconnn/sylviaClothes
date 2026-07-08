package com.sylvia.clothes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * <pre>
 * ClothesDTO
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothesDTO {

  private String id;

  @NotBlank(message = "衣物名稱不能為空")
  private String name;

  private String brand;

  @NotBlank(message = "類別不能為空")
  private String category;

  private String season;

  private String color;

  private String style;

  private String price;

  private String image; // Base64 encoded

  private String note;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
