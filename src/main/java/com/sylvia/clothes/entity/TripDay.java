package com.sylvia.clothes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * TripDay (旅行日) - 第二層：屬於某個旅行的單一日期，
 * 可以掛上多件已建立的衣物（上衣、褲子...等可複選），並可填寫當日備註
 * </pre>
 *
 * @author Eden
 */
@Entity
@Table(name = "trip_day")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDay {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  @JsonIgnore
  private Trip trip;

  @Column(nullable = false)
  private LocalDate date; // 該日日期

  @Column(nullable = false)
  private int dayIndex; // DAY 1, DAY 2 ...

  @Column(columnDefinition = "TEXT")
  private String note; // 備註（天氣/活動...）

  @Builder.Default
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "trip_day_clothes",
      joinColumns = @JoinColumn(name = "trip_day_id"),
      inverseJoinColumns = @JoinColumn(name = "clothes_id")
  )
  @OrderBy("category ASC")
  private List<Clothes> clothesList = new ArrayList<>();
}
