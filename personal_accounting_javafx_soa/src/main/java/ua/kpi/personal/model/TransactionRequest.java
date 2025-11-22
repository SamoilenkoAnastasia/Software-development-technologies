package ua.kpi.personal.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRequest {
    private Long accountId;
    private BigDecimal amount;
    private String title;
    private String type; 
    private LocalDateTime timestamp;

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
