package com.everton.FinTrack.services;

import com.everton.FinTrack.dtos.BoxSummaryDto;
import com.everton.FinTrack.dtos.TransactionRequestDto;
import com.everton.FinTrack.dtos.TransactionResponseDto;
import com.everton.FinTrack.entities.Transaction;
import com.everton.FinTrack.enums.Type;

import java.util.List;

public interface TransactionService {
    public Transaction createTransaction(TransactionRequestDto requestDto, String fileId, String fileUrl);
    public TransactionResponseDto findTransactionById(Long id);
    public List<TransactionResponseDto> allTransaction();
    public List<TransactionResponseDto> findTransactionByDate(int year, int month);
    public List<TransactionResponseDto> findTransactionByType(Type type);
    public Transaction updateTransaction(Long id, TransactionRequestDto updateTransaction);
    public void deletedTransaction(Long id);
    public Transaction searchById(Long id);
    public BoxSummaryDto calculateTotalBalance(int year, int month);
    public BoxSummaryDto getReportByYear(int year);


}
