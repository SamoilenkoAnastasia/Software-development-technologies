/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// com.kpi.pa.service.dto.TransactionDTO.java

package com.kpi.pa.service.dto;

import java.time.LocalDateTime;

// Цей клас більше НЕ використовує Lombok

public class TransactionDTO {
    
    // 1. Поля (Attributes)
    private String type; // Дохід/Витрата
    private double amount;
    private LocalDateTime date;
    private String description;
    
    private String accountName; 
    private String categoryName;

    // 2. Конструктор з усіма аргументами (потрібен для TransactionController)
    public TransactionDTO(String type, double amount, LocalDateTime date, String description, String accountName, String categoryName) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.accountName = accountName;
        this.categoryName = categoryName;
    }
    
    // 3. Порожній конструктор (часто потрібен для FXML або фреймворків)
    public TransactionDTO() {
    }

    // 4. Гетери (Getters) - для FinanceServiceImpl
    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    // 5. Сетери (Setters) - для гнучкості, хоча DTO зазвичай не змінюють
    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}