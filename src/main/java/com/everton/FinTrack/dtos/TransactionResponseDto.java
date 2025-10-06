package com.everton.FinTrack.dtos;

import com.everton.FinTrack.enums.Category;
import com.everton.FinTrack.enums.MethodPayment;
import com.everton.FinTrack.enums.Type;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionResponseDto {

    private Long id;

    private BigDecimal value;

    private LocalDate date;

    private Type type; //Entry or Expense

    private MethodPayment payment; // pix,debit,credit,money

    private Category category; // Food,Fuel,maintenance,leisure, expense_bill

    private String observation;

    private String receiptFileId;

    private String receiptFileUrl;
}
