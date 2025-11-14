package ua.kpi.personal.repo;

import ua.kpi.personal.model.TransactionTemplate;
import ua.kpi.personal.model.Category;
import ua.kpi.personal.model.Account;
import ua.kpi.personal.model.User;
import ua.kpi.personal.util.Db; // Використовуємо ваш клас Db
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateDao {

    
    private final CategoryDao categoryDao = new CategoryDao();
    private final AccountDao accountDao = new AccountDao();

    
    public TransactionTemplate create(TransactionTemplate template) {
        String sql = "INSERT INTO transaction_templates (name, user_id, type, default_amount, description, category_id, account_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
                 
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
           
            ps.setString(1, template.getName());
  
            ps.setLong(2, template.getUser() != null ? template.getUser().getId() : 0);
            
            ps.setString(3, template.getType());
                
            if (template.getDefaultAmount() != null) {
                ps.setDouble(4, template.getDefaultAmount());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }
            
            ps.setString(5, template.getDescription());

            if (template.getCategory() != null) {
                ps.setLong(6, template.getCategory().getId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            if (template.getAccount() != null) {
                ps.setLong(7, template.getAccount().getId());
            } else {
                ps.setNull(7, Types.BIGINT);
            }
            
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    template.setId(keys.getLong(1));
                } else {
                    throw new SQLException("Creating template failed, no ID obtained.");
                }
            }
            
            return template;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public List<TransactionTemplate> findByUserId(Long userId) {
        List<TransactionTemplate> templates = new ArrayList<>();

        String sql = "SELECT t.id, t.name, t.type, t.default_amount, t.description, " +
                     "c.id AS category_id, c.name AS category_name, " +
                     "a.id AS account_id, a.name AS account_name " +
                     "FROM transaction_templates t " +
                     "LEFT JOIN categories c ON t.category_id = c.id " +
                     "LEFT JOIN accounts a ON t.account_id = a.id " +
                     "WHERE t.user_id = ?";
                 
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TransactionTemplate t = mapResultSetToTemplate(rs, userId);
                    templates.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return templates;
    }


    public boolean delete(Long templateId) {
        String sql = "DELETE FROM transaction_templates WHERE id = ?";
        
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, templateId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private TransactionTemplate mapResultSetToTemplate(ResultSet rs, Long userId) throws SQLException {
        TransactionTemplate t = new TransactionTemplate();
        
      
        t.setId(rs.getLong("id"));
        t.setName(rs.getString("name"));
        t.setType(rs.getString("type"));
        t.setDescription(rs.getString("description"));

  
        double amount = rs.getDouble("default_amount");
        if (!rs.wasNull()) {
            t.setDefaultAmount(amount);
        }
        
       
        User user = new User();
        user.setId(userId);
        t.setUser(user);

     
        Long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            Category cat = new Category();
            cat.setId(categoryId);
            cat.setName(rs.getString("category_name")); 
            t.setCategory(cat);
        }
      
        Long accountId = rs.getLong("account_id");
        if (!rs.wasNull()) {
            Account acc = new Account();
            acc.setId(accountId);
            acc.setName(rs.getString("account_name"));
            t.setAccount(acc);
        }

        return t;
    }
}