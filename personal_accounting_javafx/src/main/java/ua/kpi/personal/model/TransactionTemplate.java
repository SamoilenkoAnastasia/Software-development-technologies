/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.kpi.personal.model;

import java.time.LocalDateTime;

public class TransactionTemplate implements Cloneable {
    private Long id;
    private String name; // Назва шаблону (ключовий атрибут)
    private String type; // Тип: EXPENSE/INCOME
    private Double defaultAmount; 
    private Category category;
    private Account account;
    private User user;
    private String description;

    // Конструктор за замовчуванням
    public TransactionTemplate() {}

    // Реалізація патерну "Прототип" (Поверхневе копіювання)
    @Override
    public TransactionTemplate clone() {
        try {
            // Використовуємо вбудований метод для копіювання примітивних полів та посилань
            return (TransactionTemplate) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError("CloneNotSupportedException shouldn't happen for TransactionTemplate.", e);
        }
    }
    
    // Ключовий метод для створення реальної ТРАНЗАКЦІЇ з цього ШАБЛОНУ
    public Transaction createTransactionFromTemplate() {
        // Клонуємо прототип і конвертуємо його в об'єкт Transaction
        
        Transaction tx = new Transaction();
        
        tx.setType(this.type);
        tx.setAmount(this.defaultAmount != null ? this.defaultAmount : 0.0);
        
        // Посилання на Category, Account та User є поверхневими, що тут коректно,
        // оскільки ці об'єкти існують в БД і не клонуються.
        tx.setCategory(this.category); 
        tx.setAccount(this.account);   
        tx.setUser(this.user);         
        
        tx.setDescription(this.description);
        tx.setCreatedAt(LocalDateTime.now()); // Встановлюємо нову дату
        // ID нової транзакції буде встановлено DAO при збереженні
        
        return tx;
    }

    // --- Геттери та Сеттери ---
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
    
    // Для відображення в ChoiceBox
    @Override
    public String toString() {
        return name; 
    }
}