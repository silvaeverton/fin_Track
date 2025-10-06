package com.everton.FinTrack.services.Impl;

import com.everton.FinTrack.entities.Transaction;
import com.everton.FinTrack.entities.YearSummary;
import com.everton.FinTrack.repositories.TransactionRepository;
import com.everton.FinTrack.repositories.YearSummaryRepository;
import com.everton.FinTrack.services.DataCleanupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataCleanupServiceImpl implements DataCleanupService {

    private final YearSummaryRepository  yearSummaryRepository;
    private final TransactionRepository transactionRepository;


    @Override
    @Transactional
    public void archiveAndDeleteYear(int year) {

        // Busca todas as transações do ano inteiro (inclusive jan/fev)
        List<Transaction> transactions = transactionRepository.findByYear(year);

        if (transactions.isEmpty()) return;

        BigDecimal entry = transactions.stream()
                .filter(t -> "ENTRY".equals(t.getType()))
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = entry.subtract(expense);

        YearSummary summary = new YearSummary();
        summary.setYear(year);
        summary.setTotalEntradas(entry);
        summary.setTotalSaidas(expense);
        summary.setSaldoFinal(balance);

        yearSummaryRepository.saveAndFlush(summary);

        // Exclui o ano inteiro (jan a dez)
        transactionRepository.deleteAll(transactions);
    }
}


