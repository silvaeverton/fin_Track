package com.everton.FinTrack.enums;

import lombok.Getter;

@Getter
public enum MethodPayment {
    PIX("Pix"),
    CREDIT("Crédito"),
    DEBIT("Débito"),
    MONEY("Dinheiro");

    private final String label;

    MethodPayment(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}