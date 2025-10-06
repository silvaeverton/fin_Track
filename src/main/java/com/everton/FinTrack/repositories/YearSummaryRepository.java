package com.everton.FinTrack.repositories;

import com.everton.FinTrack.entities.YearSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YearSummaryRepository extends JpaRepository<YearSummary,Long> {
    Optional<YearSummary> findByYear(int year);
}
