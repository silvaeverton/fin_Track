package com.everton.FinTrack.mappers;

import com.everton.FinTrack.dtos.TransactionResponseDto;
import com.everton.FinTrack.entities.Transaction;

import java.util.List;

public class TransactionMapper {

    public static TransactionResponseDto toDto(Transaction transaction) {

        if(transaction == null) return null;

        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(transaction.getId());
        dto.setDate(transaction.getDate());
        dto.setType(transaction.getType());
        dto.setValue(transaction.getValue());
        dto.setPayment(transaction.getPayment());
        dto.setCategory(transaction.getCategory());
        dto.setObservation(transaction.getObservation());
        dto.setReceiptFileId(transaction.getReceiptFileId());
        dto.setReceiptFileUrl(transaction.getReceiptFileUrl());

        return dto;
    }

    public static List<TransactionResponseDto> toListDto (List<Transaction> list) {

        if(list == null) return null;

        return list.stream()
                .map(TransactionMapper::toDto)
                .toList();
    }
}
