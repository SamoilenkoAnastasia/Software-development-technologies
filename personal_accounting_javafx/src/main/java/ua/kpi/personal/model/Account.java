package ua.kpi.personal.model;

public class Account {
    private Long id;
    private User user;
    private String name;
    private Double balance;
    private String type;
    private String currency;

    // 1. Методи для ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 2. Методи для Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 3. Методи для Balance
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    // 4. Методи для Type (виправлені раніше)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // 5. Методи для Currency (виправлені раніше)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    // 6. Методи для User (виправлені раніше)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
public String toString() {
    // Формат для відображення у ChoiceBox: "Депозит (UAH 1500.00)"
    String balanceStr = balance != null ? String.format(" %.2f", balance) : "0.00";
    return name + " (" + currency + balanceStr + ")";
}
}

