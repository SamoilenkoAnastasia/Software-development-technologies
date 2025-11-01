package ua.kpi.personal.repo;

import ua.kpi.personal.model.Category;
import ua.kpi.personal.model.User;
import ua.kpi.personal.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    
    /**
     * Знаходить усі категорії, що належать користувачу або є загальнодоступними (user_id IS NULL).
     * @param userId ID користувача.
     * @return Список об'єктів Category.
     */
    public List<Category> findByUserId(Long userId){ 
        var list = new ArrayList<Category>();
        // ЗМІНА: Додано стовпець type у SELECT
        String sql = "SELECT id, user_id, name, type FROM categories WHERE user_id = ? OR user_id IS NULL ORDER BY name";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) { 
            
            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    Category cObj = new Category();
                    cObj.setId(rs.getLong("id"));
                    cObj.setName(rs.getString("name"));
                    // ЗМІНА: Встановлення типу (type)
                    cObj.setType(rs.getString("type")); 
                    
                    // Створення мінімального об'єкта User
                    Long uid = rs.getLong("user_id");
                    if (!rs.wasNull()) {
                        User u = new User();
                        u.setId(uid);
                        cObj.setUser(u);
                    }
                    
                    list.add(cObj);
                }
            }
        } catch(SQLException e){ e.printStackTrace(); }
        return list;
    }
    
    /**
     * Створює нову категорію в базі даних.
     * @param category Об'єкт Category для створення.
     * @return Об'єкт Category зі встановленим ID.
     */
    public Category create(Category category){
        // ЗМІНА: Додано стовпець type в INSERT
        String sql = "INSERT INTO categories (user_id, name, type) VALUES (?,?,?)";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            ps.setObject(1, category.getUser()!=null?category.getUser().getId():null); 
            ps.setString(2, category.getName());
            // ЗМІНА: Використовуємо category.getType()
            ps.setString(3, category.getType()); 
            
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if(keys.next()) category.setId(keys.getLong(1));
            }
            return category;
        } catch(SQLException e){ e.printStackTrace(); return null; }
    }
}