package ua.kpi.personal.model;

public class Account {
    private Long id;
    private User user;
    private String name;
    private Double balance;
    private String type;
    private String currency;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

   
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
public String toString() {
    
    String balanceStr = balance != null ? String.format(" %.2f", balance) : "0.00";
    return name + " (" + currency + balanceStr + ")";
}
}

