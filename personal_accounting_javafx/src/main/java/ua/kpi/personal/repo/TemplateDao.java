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

            // --- Встановлення параметрів запиту ---
            ps.setString(1, template.getName());
            
            // Встановлення user_id
            ps.setLong(2, template.getUser() != null ? template.getUser().getId() : 0);
            
            ps.setString(3, template.getType());
            
            // Встановлення default_amount (з обробкою NULL)
            if (template.getDefaultAmount() != null) {
                ps.setDouble(4, template.getDefaultAmount());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }
            
            ps.setString(5, template.getDescription());
            
            // Встановлення зовнішніх ключів (Category ID та Account ID)
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
            
            // Отримання згенерованого ID
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
        
        // Використовуємо JOIN, щоб отримати імена Category та Account, а не лише ID.
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

    /**
     * Видаляє шаблон транзакції з бази даних за ID.
     * @param templateId ID шаблону для видалення.
     * @return true, якщо видалення пройшло успішно, інакше false.
     */
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
        
        // Встановлення примітивних полів
        t.setId(rs.getLong("id"));
        t.setName(rs.getString("name"));
        t.setType(rs.getString("type"));
        t.setDescription(rs.getString("description"));

        // Встановлення default_amount (з обробкою NULL)
        double amount = rs.getDouble("default_amount");
        if (!rs.wasNull()) {
            t.setDefaultAmount(amount);
        }
        
        // Встановлення користувача
        User user = new User();
        user.setId(userId);
        t.setUser(user);

        // --- Завантаження Залежностей (Category) ---
        Long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            Category cat = new Category();
            cat.setId(categoryId);
            cat.setName(rs.getString("category_name")); 
            t.setCategory(cat);
        }
        
        // --- Завантаження Залежностей (Account) ---
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