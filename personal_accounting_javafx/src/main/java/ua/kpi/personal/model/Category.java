package ua.kpi.personal.model;

import java.time.LocalDateTime;

public class Category {
    
    private Long id;
    private User user;
    private String name;
    private String type;
    private LocalDateTime createdAt;
    
    // --- Поля та конструктори (якщо вони є) ---
    
    // --- Геттери та Сеттери ---
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    // Додані методи:
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
public String toString() {
    // Формат для відображення у ChoiceBox: "Зарплата"
    return name;
}

} 