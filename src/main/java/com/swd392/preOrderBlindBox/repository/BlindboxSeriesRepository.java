package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindboxSeriesRepository extends JpaRepository<BlindboxSeries, Long> {
}
