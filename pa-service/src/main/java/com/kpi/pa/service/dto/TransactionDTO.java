package com.kpi.pa.service.dto;

import com.kpi.pa.service.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String type;
    private String description;
    private String currency;
    private String title;
    private LocalDateTime createdAt;

    public TransactionDTO() {}

    public TransactionDTO(Transaction t) {
        this.id = t.getId();
        this.userId = t.getUser() != null ? t.getUser().getId() : null;
        this.username = t.getUser() != null ? t.getUser().getUsername() : null;
        this.accountId = t.getAccount() != null ? t.getAccount().getId() : null;
        this.accountName = t.getAccount() != null ? t.getAccount().getName() : null;
        this.categoryId = t.getCategory() != null ? t.getCategory().getId() : null;
        this.categoryName = t.getCategory() != null ? t.getCategory().getName() : null;
        this.amount = t.getAmount();
        this.type = t.getType();
        this.description = t.getDescription();
        this.currency = t.getCurrency();
        this.title = t.getTitle();
        this.createdAt = t.getCreatedAt();
    }

    public static TransactionDTO fromEntity(Transaction t) {
        return new TransactionDTO(t);
    }

    // --- Getters і Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
