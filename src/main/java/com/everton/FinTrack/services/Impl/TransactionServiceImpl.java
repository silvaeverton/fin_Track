package com.everton.FinTrack.services.Impl;

import com.everton.FinTrack.dtos.BoxSummaryDto;
import java.util.concurrent.CompletableFuture;
import com.everton.FinTrack.dtos.TransactionRequestDto;
import com.everton.FinTrack.dtos.TransactionResponseDto;
import com.everton.FinTrack.entities.Transaction;
import com.everton.FinTrack.enums.Type;
import com.everton.FinTrack.exceptions.custom.NotFoundException;
import com.everton.FinTrack.mappers.TransactionMapper;
import com.everton.FinTrack.repositories.TransactionRepository;
import com.everton.FinTrack.repositories.YearSummaryRepository;
import com.everton.FinTrack.services.TransactionService;
import com.everton.FinTrack.services.Impl.GoogleDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final YearSummaryRepository yearSummaryRepository;
    private final GoogleDriveService googleDriveService;
   private final ReceiptService receiptService; 

    @Override
   public Transaction createTransaction(TransactionRequestDto requestDto, String fileId, String fileUrl) {
    Transaction transaction = new Transaction();

    transaction.setObservation(requestDto.getObservation());
    transaction.setValue(requestDto.getValue());
    transaction.setType(requestDto.getType());
    transaction.setCategory(requestDto.getCategory());
    transaction.setPayment(requestDto.getPayment());
    transaction.setDate(requestDto.getDate());

    // Salva imediatamente no banco
    Transaction savedTransaction = transactionRepository.save(transaction);

    // Se tiver arquivo, faz o upload no Google Drive de forma assíncrona
    if (requestDto.getFile() != null && !requestDto.getFile().isEmpty()) {
        CompletableFuture.runAsync(() -> {
            try {
                String uploadedFileId = googleDriveService.uploadFile(requestDto.getFile());
                String driveUrl = googleDriveService.generateDriveFileLink(uploadedFileId);

                // Atualiza o registro no banco após o upload
                savedTransaction.setReceiptFileId(uploadedFileId);
                savedTransaction.setReceiptFileUrl(driveUrl);
                transactionRepository.save(savedTransaction);

                System.out.println("✅ Upload concluído e transação atualizada: " + uploadedFileId);
            } catch (Exception e) {
                System.err.println("❌ Erro ao enviar arquivo para o Drive: " + e.getMessage());
            }
        });
    }

    return savedTransaction;
}

    @Override
    public TransactionResponseDto findTransactionById(Long id) {
        Transaction transaction = searchById(id);

      return TransactionMapper.toDto(transaction);

    }

    @Override
    public List<TransactionResponseDto> allTransaction() {
        List<Transaction> list = transactionRepository.findAll();

        if(list.isEmpty()) {
          return Collections.emptyList();
        }
        return  TransactionMapper.toListDto(list);
    }

    @Override
    public List<TransactionResponseDto> findTransactionByDate(int year, int month) {
        LocalDate start = LocalDate.of(year,month,1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> list = transactionRepository.findByDateBetween(start,end);

        if(list.isEmpty()) {
           return Collections.emptyList();
        }

        return TransactionMapper.toListDto(list);
    }

    @Override
    public List<TransactionResponseDto> findTransactionByType(Type type) {
        List<Transaction> list = transactionRepository.findByType(type);

        if(list.isEmpty()) {
            return Collections.emptyList();
        }
        return TransactionMapper.toListDto(list);
    }

    @Override
    public Transaction updateTransaction(Long id, TransactionRequestDto updateTransaction) {
        Transaction transaction = searchById(id);

        if(updateTransaction.getValue() != null) {transaction.setValue(updateTransaction.getValue());}
        if(updateTransaction.getDate() != null) { transaction.setDate(updateTransaction.getDate());}
        if(updateTransaction.getType() != null) {transaction.setType(updateTransaction.getType());}
        if(updateTransaction.getPayment() != null) {transaction.setPayment(updateTransaction.getPayment());}
        if(updateTransaction.getObservation() != null) {transaction.setObservation(updateTransaction.getObservation());}

        return transactionRepository.saveAndFlush(transaction);
    }

    @Override
    public void deletedTransaction(Long id) {

        transactionRepository.deleteById(id);

    }

    @Override
    public Transaction searchById(Long id) {
      return transactionRepository.findById(id).orElseThrow(()-> new NotFoundException(
                "Transaction not found",404
        ));
    }

    @Override
    public BoxSummaryDto calculateTotalBalance(int year, int month) {

        LocalDate start = LocalDate.of(year,month,1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> list = transactionRepository.findByDateBetween(start,end);

        BigDecimal entry = list.stream().filter(
                transaction -> Type.ENTRY.equals(transaction.getType()))
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal expense = list.stream().filter(
                transaction -> Type.EXPENSE.equals(transaction.getType()))
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal total = entry.subtract(expense);

        return new  BoxSummaryDto(entry,expense,total);
    }

    @Override
    public BoxSummaryDto getReportByYear(int year) {

        return yearSummaryRepository.findByYear(year)
                .map(summary -> {
                    BoxSummaryDto dto = new BoxSummaryDto();
                    dto.setTotalEntry(summary.getTotalEntradas());
                    dto.setTotalExpense(summary.getTotalSaidas());
                    dto.setBalance(summary.getSaldoFinal());
                    return dto;
                })

                .orElseGet(() -> {
                    List<Transaction> transactions = transactionRepository.findByYear(year);

                    BigDecimal entry = transactions.stream()
                            .filter(t -> t.getType().equals("ENTRADA"))
                            .map(Transaction::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal expense = transactions.stream()
                            .filter(t -> t.getType().equals("SAIDA"))
                            .map(Transaction::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal balance = entry.subtract(expense);

                    BoxSummaryDto dto = new BoxSummaryDto();
                    dto.setTotalEntry(entry);
                    dto.setTotalExpense(expense);
                    dto.setBalance(balance);
                    return dto;
                });
    }

}
