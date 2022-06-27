package com.yifang.exchange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByCode(String code);

    List<Stock> findAllByTags(String tag);

    @Modifying
    @Query(value = "DELETE FROM stock_tags tags WHERE tags.tag = ?1", nativeQuery = true)
    void deleteStockTags(String tag);

    @Modifying
    @Query(value = "DELETE FROM snap_shot_tags tags WHERE tags.tag = ?1", nativeQuery = true)
    void deleteSnapShotTags(String tag);
}
