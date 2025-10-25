/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kpi.pa.domain;

import java.time.LocalDateTime;

public class Transaction {
    
    private Long id;
    private Long accountId; 
    private Long categoryId;
    private String type;
    private double amount; // Змінено на double
    private LocalDateTime date; 
    private String description;
    private String receiptImagePath; 
    
    public Transaction() {}
    
    // Гетери
    public Long getId() { return id; }
    public double getAmount() { return amount; } // Гетер для double
    public String getType() { return type; } 
    public LocalDateTime getDate() { return date; }
    public String getDescription() { return description; }
    // ... інші гетери
    
    // Сетери
    public void setId(Long id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; } // Сетер для double
    public void setType(String type) { this.type = type; } 
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    // ... інші сетери
}