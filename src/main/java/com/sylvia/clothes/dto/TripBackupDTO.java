package com.sylvia.clothes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * TripBackupDTO - 旅行資料備份（含每日安排與已選衣物），用法與 BackupDTO（衣物備份）相同
 * </pre>
 *
 * @author Eden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripBackupDTO {

  private List<TripDTO> trips;

  private String backupDate;

  private String version;

  public TripBackupDTO(List<TripDTO> trips) {
    this.trips = trips;
    this.backupDate = java.time.LocalDateTime.now().toString();
    this.version = "1.0.0";
  }
}
