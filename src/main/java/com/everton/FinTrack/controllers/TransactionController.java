package com.everton.FinTrack.controllers;

import com.everton.FinTrack.dtos.BoxSummaryDto;
import com.everton.FinTrack.dtos.TransactionRequestDto;
import com.everton.FinTrack.dtos.TransactionResponseDto;
import com.everton.FinTrack.entities.Transaction;
import com.everton.FinTrack.enums.Type;
import com.everton.FinTrack.services.Impl.GoogleDriveService;
import com.everton.FinTrack.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/Transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final GoogleDriveService googleDriveService;

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(
            @RequestPart("transaction") @Valid TransactionRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {

        String fileId = null;
        String fileUrl = null;

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();


            if (!List.of("image/jpeg", "image/png", "application/pdf").contains(contentType)) {
                throw new IllegalArgumentException("Formato de arquivo inválido! Envie apenas JPEG, PNG ou PDF.");
            }

            fileId = googleDriveService.uploadFile(file);
            fileUrl = googleDriveService.generateDriveFileLink(fileId);

            requestDto.setReceiptFileId(fileId);
            requestDto.setReceiptFileUrl(fileUrl);
        }

        return transactionService.createTransaction(requestDto, fileId, fileUrl);
    }



    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponseDto findTransactionById(@PathVariable Long id) {
        return transactionService.findTransactionById(id);
    }

    @GetMapping
    public List<TransactionResponseDto> allTransaction() {
        return transactionService.allTransaction();
    }

    @GetMapping("/by-month")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponseDto> findTransactionByDate(@RequestParam int year,
                                                              @RequestParam int month) {
        return transactionService.findTransactionByDate(year, month);
    }

    @GetMapping("/by-type")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponseDto> findTransactionByType(@RequestParam Type type) {
        return transactionService.findTransactionByType(type);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Transaction updateTransaction(@PathVariable Long id,
                                         @RequestBody TransactionRequestDto updateTransaction) {
        return transactionService.updateTransaction(id, updateTransaction);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletedTransaction(@PathVariable Long id) {
        transactionService.deletedTransaction(id);
    }

    public Transaction searchById(Long id) {
        return transactionService.searchById(id);
    }

    @GetMapping("box")
    @ResponseStatus(HttpStatus.OK)
    public BoxSummaryDto calculateTotalBalance(@RequestParam int year, @RequestParam int month) {
        return transactionService.calculateTotalBalance(year, month);
    }

    @GetMapping("/{year}")
    public ResponseEntity<BoxSummaryDto> getReport(@PathVariable int year) {
        BoxSummaryDto report = transactionService.getReportByYear(year);
        return ResponseEntity.ok(report);
    }
}