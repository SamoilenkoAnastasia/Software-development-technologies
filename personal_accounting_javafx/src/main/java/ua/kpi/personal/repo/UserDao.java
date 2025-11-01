package ua.kpi.personal.repo;

import ua.kpi.personal.model.User;
import ua.kpi.personal.util.Db;

import java.sql.*;

public class UserDao {

    /**
     * Знаходить користувача за його ім'ям (username).
     * Використовує стовпці: id, username, password_hash, full_name
     * @param username Ім'я користувача для пошуку.
     * @return Об'єкт User, якщо знайдено, або null.
     */
    public User findByUsername(String username){
        // ЗМІНА: Використовуємо 'username' та 'full_name' замість 'name' та 'email'
        String sql = "SELECT id, username, password_hash, full_name FROM users WHERE username = ?";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    User u = new User();
                    u.setId(rs.getLong("id"));
                    // ЗМІНА: Зчитуємо з колонки 'username'
                    u.setUsername(rs.getString("username")); 
                    // Зчитуємо з колонки 'password_hash'
                    u.setPassword(rs.getString("password_hash")); 
                    // ЗМІНА: Зчитуємо з колонки 'full_name'
                    u.setFullName(rs.getString("full_name")); 
                    return u;
                }
            }
        } catch(SQLException e){ 
            e.printStackTrace(); 
        }
        return null;
    }

    /**
     * Створює нового користувача в базі даних.
     * @param user Об'єкт User з даними для реєстрації.
     * @return Об'єкт User з встановленим ID, якщо успішно, або null у разі помилки.
     */
    public User create(User user){
        // ЗМІНА: Використовуємо 'username' та 'full_name' замість 'name' та 'email'
        String sql = "INSERT INTO users (username, password_hash, full_name) VALUES (?,?,?)";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // 1. Встановлюємо параметри відповідно до структури таблиці
            ps.setString(1, user.getUsername()); // -> username
            ps.setString(2, user.getPassword()); // -> password_hash
            ps.setString(3, user.getFullName()); // -> full_name
            
            // 2. Виконуємо запит на вставку (Виправлення: рядок, якого не вистачало)
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("Creating user failed, no rows affected.");
                return null;
            }

            // 3. Отримуємо згенерований ID (Виправлення: рядок, якого не вистачало)
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1)); 
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            
            return user;
            
        } catch(SQLException e){ 
            // Якщо виникає помилка (наприклад, дублікат username), повертаємо null
            e.printStackTrace(); 
            return null; 
        }
    }
}