package com.everton.FinTrack.repositories;

import com.everton.FinTrack.entities.Transaction;
import com.everton.FinTrack.enums.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);
    default List<Transaction> findByYear(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return findByDateBetween(start, end);
    }
    List<Transaction> findByType(Type type);
}
