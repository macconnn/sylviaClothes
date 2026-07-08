package com.sylvia.clothes.jpa;

import com.sylvia.clothes.entity.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * ClothesRepository
 * </pre>
 *
 * @author Eden
 */
@Repository
public interface ClothesRepository extends JpaRepository<Clothes, String> {

  // 按類別查詢
  List<Clothes> findByCategory(String category);

  // 按季節查詢
  List<Clothes> findBySeason(String season);

  // 按品牌查詢
  List<Clothes> findByBrand(String brand);

  // 搜尋 - 名稱、品牌、顏色
  @Query("SELECT c FROM Clothes c WHERE " +
      "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(c.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(c.color) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Clothes> searchByKeyword(@Param("keyword") String keyword);

  // 複合查詢
  @Query("SELECT c FROM Clothes c WHERE " +
      "(:category IS NULL OR c.category = :category) AND " +
      "(:season IS NULL OR c.season = :season) AND " +
      "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(c.brand) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  List<Clothes> filterClothes(@Param("category") String category,
      @Param("season") String season,
      @Param("keyword") String keyword);

  // 統計各類別數量
  @Query("SELECT c.category, COUNT(c) FROM Clothes c GROUP BY c.category")
  List<Object[]> countByCategory();
}
