package com.everton.FinTrack.controllers;

import com.everton.FinTrack.enums.Category;
import com.everton.FinTrack.enums.MethodPayment;
import com.everton.FinTrack.enums.Type;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionForm {


    private BigDecimal value;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date = LocalDate.now();

    private Type type;
    private MethodPayment payment;
    private Category category;
    private String observation;
    private String receiptFileId;


}


