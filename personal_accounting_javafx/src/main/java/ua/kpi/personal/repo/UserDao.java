package ua.kpi.personal.repo;

import ua.kpi.personal.model.User;
import ua.kpi.personal.util.Db;

import java.sql.*;

public class UserDao {

   
    public User findByUsername(String username){
        
        String sql = "SELECT id, username, password_hash, full_name FROM users WHERE username = ?";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    User u = new User();
                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username")); 
                    u.setPassword(rs.getString("password_hash")); 
                    u.setFullName(rs.getString("full_name")); 
                    return u;
                }
            }
        } catch(SQLException e){ 
            e.printStackTrace(); 
        }
        return null;
    }

    
    public User create(User user){
        
        String sql = "INSERT INTO users (username, password_hash, full_name) VALUES (?,?,?)";
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            
            ps.setString(1, user.getUsername()); 
            ps.setString(2, user.getPassword()); 
            ps.setString(3, user.getFullName()); 
            
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("Creating user failed, no rows affected.");
                return null;
            }

            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1)); 
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            
            return user;
            
        } catch(SQLException e){ 
            e.printStackTrace(); 
            return null; 
        }
    }
}