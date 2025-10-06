package com.everton.FinTrack.entities;

import com.everton.FinTrack.enums.Category;
import com.everton.FinTrack.enums.MethodPayment;
import com.everton.FinTrack.enums.Type;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal value;

    private LocalDate date;

    private Type type; //Entry or Expense

    private MethodPayment payment; // pix,debit,credit,money

    private Category category; // Food,Fuel,maintenance,leisure, expense_bill

    private String observation;

    @Column(name = "file_id")
    private String receiptFileId;

    private String receiptFileUrl;

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

}
