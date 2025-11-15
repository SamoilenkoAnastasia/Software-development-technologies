package ua.kpi.personal.repo;

import ua.kpi.personal.model.Category;
import ua.kpi.personal.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class CategoryDao {
    
    
    public List<Category> findByUserId(Long userId){ 
        var list = new ArrayList<Category>();
        String sql = "SELECT id, user_id, name, type, parent_id, created_at FROM categories WHERE user_id = ? OR user_id IS NULL ORDER BY name";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) { 
            
            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    Long id = rs.getLong("id");
                    Long uid = rs.getLong("user_id");
                    if (rs.wasNull()) uid = null;
                    
                    Long parentId = rs.getLong("parent_id");
                    if (rs.wasNull()) parentId = null;

                    Category cObj = new Category(
                        id,
                        uid,
                        rs.getString("name"),
                        rs.getString("type"),
                        parentId,
                        rs.getTimestamp("created_at").toLocalDateTime() 
                    );
                    
                    list.add(cObj);
                }
            }
        } catch(SQLException e){ e.printStackTrace(); }
        
        return list;
    }
    
    
    public Category create(Category category){
        String sql = "INSERT INTO categories (user_id, name, type, parent_id, created_at) VALUES (?,?,?,?,?)";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            ps.setObject(1, category.getUserId()); 
            ps.setString(2, category.getName()); 
            ps.setString(3, category.getType()); 
            ps.setObject(4, category.getParentId()); 
            ps.setTimestamp(5, Timestamp.valueOf(category.getCreatedAt())); 
            
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if(keys.next()) {
                    Category created = category.withId(keys.getLong(1)); 
                    return created;
                }
            }
            return category;
        } catch(SQLException e){ e.printStackTrace(); return null; }
    }
    
 
    public boolean update(Category category) {
        
        String sql = "UPDATE categories SET name = ?, type = ?, parent_id = ? WHERE id = ? AND user_id = ?";
        
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getType());
            ps.setObject(3, category.getParentId());
            ps.setLong(4, category.getId());
            ps.setLong(5, category.getUserId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public boolean delete(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
    
 
    public Category findById(Long id) {
        String sql = "SELECT id, user_id, name, type, parent_id, created_at FROM categories WHERE id = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long uid = rs.getLong("user_id");
                    if (rs.wasNull()) uid = null;
                    
                    Long parentId = rs.getLong("parent_id");
                    if (rs.wasNull()) parentId = null;
                    
                    return new Category(
                        rs.getLong("id"),
                        uid,
                        rs.getString("name"),
                        rs.getString("type"),
                        parentId,
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}