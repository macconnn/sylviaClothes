/**
 * EZTRAVEL CONFIDENTIAL
 *
 * @Package: com.sylvia.clothes.dto
 * @FileName: ClothesDTO.java
 * @author: Eden
 * @date: 2026/7/8, 下午 01:57
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
