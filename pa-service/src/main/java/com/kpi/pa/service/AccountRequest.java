package com.kpi.pa.service;

import java.math.BigDecimal;


public class AccountRequest {
    private String name;
    private BigDecimal initialBalance;
    private String currency; // Необов'язково

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}