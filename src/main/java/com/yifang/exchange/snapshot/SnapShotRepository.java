package com.yifang.exchange.snapshot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapShotRepository extends JpaRepository<SnapShot, Long> {
    List<SnapShot> findByMonth(int month);
}
