package com.sylvia.clothes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Clothes
 * </pre>
 *
 * @author Eden
 */
@Entity
@Table(name = "clothes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clothes {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(length = 100)
  private String brand;

  @Column(nullable = false)
  private String category; // 上衣, 褲子, 裙子, 外套, 洋裝, 鞋子, 包包, 配件, 內搭, 運動, 其他

  @Column(length = 50)
  private String season; // 四季, 春夏, 秋冬, 夏, 冬

  @Column(length = 50)
  private String color;

  @Column(length = 100)
  private String style; // 休閒, 正式, 韓系等

  @Column(length = 20)
  private String price; // NT$

  @Lob
  @Column(columnDefinition = "TEXT")
  private String image; // Base64 encoded image

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
