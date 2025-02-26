package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindboxSeriesRepository extends JpaRepository<BlindboxSeries, Long> {
    @Query("SELECT DISTINCT bs FROM BlindboxSeries bs " +
            "LEFT JOIN FETCH bs.blindboxUnits " +
            "LEFT JOIN FETCH bs.blindboxAssets " +
            "LEFT JOIN FETCH bs.blindboxSeriesItems")
    List<BlindboxSeries> findAll();
}
