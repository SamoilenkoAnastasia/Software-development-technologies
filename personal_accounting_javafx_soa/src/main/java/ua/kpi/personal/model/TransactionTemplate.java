package ua.kpi.personal.model;

import java.time.LocalDateTime;

public class TransactionTemplate implements Cloneable {
    private Long id;
    private String name; 
    private String type; 
    private Double defaultAmount; 
    private Category category;
    private Account account;
    private User user;
    private String description;

    
    public TransactionTemplate() {}

    @Override
    public TransactionTemplate clone() {
        try {
            return (TransactionTemplate) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError("CloneNotSupportedException shouldn't happen for TransactionTemplate.", e);
        }
    }
    
   
    public Transaction createTransactionFromTemplate() {
        
        Transaction tx = new Transaction();
        
        tx.setType(this.type);
        tx.setAmount(this.defaultAmount != null ? this.defaultAmount : 0.0);

        tx.setCategory(this.category); 
        tx.setAccount(this.account);   
        tx.setUser(this.user);         
        
        tx.setDescription(this.description);
        tx.setCreatedAt(LocalDateTime.now()); 
  
        return tx;
    }

   
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getDefaultAmount() { return defaultAmount; }
    public void setDefaultAmount(Double defaultAmount) { this.defaultAmount = defaultAmount; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return name; 
    }
}