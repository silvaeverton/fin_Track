package com.everton.FinTrack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoxSummaryDto {

    private BigDecimal totalEntry;
    private BigDecimal totalExpense;
    private BigDecimal balance;


}
