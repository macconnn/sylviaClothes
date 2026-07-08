package com.sylvia.clothes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * BackupDTO
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupDTO {

  private List<ClothesDTO> clothes;

  private String backupDate;

  private String version;

  public BackupDTO(List<ClothesDTO> clothes) {
    this.clothes = clothes;
    this.backupDate = java.time.LocalDateTime.now().toString();
    this.version = "1.0.0";
  }
}
