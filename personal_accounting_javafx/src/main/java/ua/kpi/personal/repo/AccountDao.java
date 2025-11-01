package ua.kpi.personal.repo;

import ua.kpi.personal.model.Account;
import ua.kpi.personal.model.User;
import ua.kpi.personal.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    
    /**
     * Знаходить усі рахунки, що належать конкретному користувачу.
     * @param userId ID користувача.
     * @return Список об'єктів Account.
     */
    public List<Account> findByUserId(Long userId){ 
        var list = new ArrayList<Account>();
        // ЗМІНА: Додано стовпці type, currency у SELECT
        String sql = "SELECT id, user_id, name, type, currency, balance FROM accounts WHERE user_id = ?";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) { 
            
            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    Account a = new Account();
                    a.setId(rs.getLong("id"));
                    a.setName(rs.getString("name"));
                    // ЗМІНА: Встановлення типу (type) та валюти (currency)
                    a.setType(rs.getString("type"));
                    a.setCurrency(rs.getString("currency"));
                    a.setBalance(rs.getDouble("balance")); 
                    
                    // Створення мінімального об'єкта User
                    User u = new User();
                    u.setId(rs.getLong("user_id"));
                    a.setUser(u);
                    
                    list.add(a);
                }
            }
        } catch(SQLException e){ e.printStackTrace(); }
        return list;
    }
    
    /**
     * Знаходить рахунок за ID, перевіряючи приналежність користувачу.
     * @param id ID рахунку.
     * @param userId ID користувача, якому належить рахунок.
     * @return Об'єкт Account або null.
     */
    public Account findById(Long id, Long userId){ 
        // ЗМІНА: Додано стовпці type, currency у SELECT
        String sql = "SELECT id, user_id, name, type, currency, balance FROM accounts WHERE id = ? AND user_id = ?";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) { 
            
            ps.setLong(1, id);
            ps.setLong(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    Account a = new Account();
                    a.setId(rs.getLong("id"));
                    a.setName(rs.getString("name"));
                    // ЗМІНА: Встановлення типу (type) та валюти (currency)
                    a.setType(rs.getString("type"));
                    a.setCurrency(rs.getString("currency"));
                    a.setBalance(rs.getDouble("balance"));
                    
                    // Створення мінімального об'єкта User
                    User u = new User();
                    u.setId(rs.getLong("user_id"));
                    a.setUser(u);
                    
                    return a;
                }
            }
        } catch(SQLException e){ e.printStackTrace(); }
        return null;
    }
    
    /**
     * Оновлює інформацію про рахунок (зокрема баланс).
     * @param account Об'єкт Account з оновленими даними.
     * @return Оновлений об'єкт Account.
     */
    public Account update(Account account){
        // ЗМІНА: Додано type=? та currency=? в UPDATE
        String sql = "UPDATE accounts SET name=?, balance=?, type=?, currency=? WHERE id=? AND user_id=?";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) { 
            
            ps.setString(1, account.getName());
            ps.setDouble(2, account.getBalance()==null?0.0:account.getBalance());
            // ЗМІНА: Використовуємо account.getType()
            ps.setString(3, account.getType());
            // ЗМІНА: Використовуємо account.getCurrency()
            ps.setString(4, account.getCurrency());
            ps.setLong(5, account.getId());
            ps.setObject(6, account.getUser()!=null?account.getUser().getId():null); 
            
            ps.executeUpdate();
            return account;
        } catch(SQLException e){ e.printStackTrace(); return null; }
    }
    
    /**
     * Створює новий рахунок у базі даних.
     * @param account Об'єкт Account для створення.
     * @return Об'єкт Account зі встановленим ID.
     */
    public Account create(Account account){
        // ЗМІНА: Додано type, currency в INSERT
        String sql = "INSERT INTO accounts (user_id, name, type, currency, balance) VALUES (?,?,?,?,?)";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            ps.setObject(1, account.getUser()!=null?account.getUser().getId():null); 
            ps.setString(2, account.getName());
            // ЗМІНА: Використовуємо account.getType()
            ps.setString(3, account.getType());
            // ЗМІНА: Використовуємо account.getCurrency()
            ps.setString(4, account.getCurrency());
            ps.setDouble(5, account.getBalance()==null?0.0:account.getBalance());
            
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                 if(keys.next()) account.setId(keys.getLong(1));
            }
            return account;
        } catch(SQLException e){ e.printStackTrace(); return null; }
    }
}