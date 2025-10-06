package com.everton.FinTrack.enums;

import lombok.Getter;

@Getter
public enum Type {
    ENTRY("Entrada"),
    EXPENSE("Despesa");

    private final String label;

    Type(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}