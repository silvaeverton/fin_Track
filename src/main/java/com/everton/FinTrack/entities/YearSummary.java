package com.everton.FinTrack.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class YearSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int year;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalEntradas;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalSaidas;

    @Column(precision = 15, scale = 2)
    private BigDecimal saldoFinal;

    private LocalDateTime createdAt = LocalDateTime.now();
}

