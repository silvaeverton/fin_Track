package com.everton.FinTrack.enums;

import lombok.Getter;

@Getter
public enum Category {
    FOOD("Alimentação"),
    FUEL("Combustível"),
    LEISURE("Lazer"),
    EXPENSE_BILL("Despesa fixa"),
    MAINTENANCE("Manutenção"),
    ENTRY("Entrada");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}