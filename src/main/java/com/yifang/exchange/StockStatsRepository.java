package com.yifang.exchange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockStatsRepository extends JpaRepository<StockStats, Long> {
    Optional<StockStats> findByCode(String code);

    @Modifying
    @Query(value = "UPDATE StockStats SET rsi = -1 ")
    void eraseStatRSI();
}
