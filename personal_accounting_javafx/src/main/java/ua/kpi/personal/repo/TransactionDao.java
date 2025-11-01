package ua.kpi.personal.repo;

import ua.kpi.personal.model.*;
import ua.kpi.personal.util.Db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionDao {

    // AccountDao та CategoryDao тепер потрібно оновлювати відповідно
    private final CategoryDao categoryDao = new CategoryDao();
    private final AccountDao accountDao = new AccountDao();

    public List<Transaction> findByUserId(Long userId){
        var list = new ArrayList<Transaction>();
        
        // ЗМІНА: Завантажуємо всі категорії та рахунки користувача заздалегідь
        List<Category> allCategories = categoryDao.findByUserId(userId);
        List<Account> allAccounts = accountDao.findByUserId(userId);
        
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT t.id, t.amount, t.type, t.description, t.created_at, t.category_id, t.account_id, t.user_id FROM transactions t WHERE t.user_id = ? ORDER BY t.created_at DESC")) {
            
            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    Transaction t = new Transaction();
                    t.setId(rs.getLong(1));
                    t.setAmount(rs.getDouble(2));
                    t.setType(rs.getString(3));
                    t.setDescription(rs.getString(4));
                    Timestamp ts = rs.getTimestamp(5);
                    if(ts!=null) t.setCreatedAt(ts.toLocalDateTime());
                    
                    Long catId = rs.getLong(6);
                    if (!rs.wasNull()) {
                        // Використовуємо заздалегідь завантажені категорії
                        t.setCategory(allCategories.stream()
                            .filter(cat -> cat.getId().equals(catId))
                            .findFirst()
                            .orElse(null));
                    }
                    
                    Long accId = rs.getLong(7);
                    if (!rs.wasNull()) {
                        // Використовуємо заздалегідь завантажені рахунки
                         t.setAccount(allAccounts.stream()
                            .filter(acc -> acc.getId().equals(accId))
                            .findFirst()
                            .orElse(null));
                    }
                    
                    // user minimal
                    User u = new User();
                    u.setId(rs.getLong(8));
                    t.setUser(u);
                    list.add(t);
                }
            }
        } catch(SQLException e){ e.printStackTrace(); }
        return list;
    }

    public Transaction create(Transaction tx){
        try(Connection c = Db.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO transactions (amount, type, description, created_at, category_id, account_id, user_id) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setDouble(1, tx.getAmount());
            ps.setString(2, tx.getType());
            ps.setString(3, tx.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(tx.getCreatedAt()));
            ps.setObject(5, tx.getCategory()!=null?tx.getCategory().getId():null);
            ps.setObject(6, tx.getAccount()!=null?tx.getAccount().getId():null);
            ps.setObject(7, tx.getUser()!=null?tx.getUser().getId():null);
            
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if(keys.next()) tx.setId(keys.getLong(1));
            }
            
            // update account balance
            if(tx.getAccount()!=null && tx.getUser()!=null){
                // ЗМІНА: Використовуємо findById з user_id
                Account acc = accountDao.findById(tx.getAccount().getId(), tx.getUser().getId()); 
                if (acc != null) {
                    double bal = acc.getBalance()==null?0.0:acc.getBalance();
                    if("EXPENSE".equalsIgnoreCase(tx.getType())) bal -= tx.getAmount();
                    else bal += tx.getAmount();
                    acc.setBalance(bal);
                    
                    // ЗМІНА: Тепер потрібно, щоб в Account був User
                    acc.setUser(tx.getUser()); 
                    accountDao.update(acc);
                }
            }
            return tx;
        } catch(SQLException e){ e.printStackTrace(); return null; }
    }
}