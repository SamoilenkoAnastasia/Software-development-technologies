package ua.kpi.personal.repo;

import ua.kpi.personal.model.*;
import ua.kpi.personal.util.Db;
import ua.kpi.personal.processor.TransactionProcessor;
import ua.kpi.personal.model.analytics.ReportParams;
import ua.kpi.personal.model.analytics.CategoryReportRow;
import ua.kpi.personal.model.analytics.MonthlyBalanceRow;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TransactionDao implements TransactionProcessor {

    private final CategoryDao categoryDao = new CategoryDao();
    private final AccountDao accountDao = new AccountDao();

    public List<CategoryReportRow> findCategorySpending(ReportParams params, User user) {
        var rows = new ArrayList<CategoryReportRow>();

        String sql = """
            SELECT c.name, SUM(t.amount)
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.user_id = ?
              AND t.type = 'EXPENSE'
              AND t.created_at BETWEEN ? AND ?
            GROUP BY c.name
            ORDER BY SUM(t.amount) DESC
        """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, user.getId());
            ps.setTimestamp(2, Timestamp.valueOf(params.getStartDate().atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(params.getEndDate().plusDays(1).atStartOfDay().minusNanos(1)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new CategoryReportRow(
                        rs.getString(1),
                        rs.getDouble(2)
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Помилка БД при отриманні витрат по категоріях: " + e.getMessage());
        }
        return rows;
    }

    public List<MonthlyBalanceRow> findMonthlyBalance(ReportParams params, User user) {

        var rows = new ArrayList<MonthlyBalanceRow>();
        String sql = """
            SELECT
                DATE_FORMAT(created_at, '%Y-%m') AS month_year,
                SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) AS total_income,
                SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) AS total_expense
            FROM transactions
            WHERE user_id = ? AND created_at BETWEEN ? AND ?
            GROUP BY month_year
            ORDER BY month_year ASC
        """;

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, user.getId());
            ps.setTimestamp(2, Timestamp.valueOf(params.getStartDate().atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(params.getEndDate().plusDays(1).atStartOfDay().minusNanos(1)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new MonthlyBalanceRow(
                        rs.getString(1),
                        rs.getDouble(2),
                        rs.getDouble(3)
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Помилка БД при отриманні динаміки по місяцях: " + e.getMessage());
        }
        return rows;
    }

   
    public List<Transaction> findTransactionsByDateRange(ReportParams params, Long userId) {
        var list = new ArrayList<Transaction>();

        List<Category> allCategories = categoryDao.findByUserId(userId);
        List<Account> allAccounts = accountDao.findByUserId(userId);

        String sql = "SELECT t.id, t.amount, t.type, t.description, t.created_at, t.category_id, t.account_id, t.user_id " +
                     "FROM transactions t " +
                     "WHERE t.user_id = ? AND t.created_at BETWEEN ? AND ? " +
                     "ORDER BY t.created_at DESC";

        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setTimestamp(2, Timestamp.valueOf(params.getStartDate().atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(params.getEndDate().plusDays(1).atStartOfDay().minusNanos(1)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getLong(1));
                    t.setAmount(rs.getDouble(2));
                    t.setType(rs.getString(3));
                    t.setDescription(rs.getString(4));
                    Timestamp ts = rs.getTimestamp(5);
                    if (ts != null) t.setCreatedAt(ts.toLocalDateTime());

                    Long catId = rs.getLong(6);
                    if (!rs.wasNull()) {
                        t.setCategory(allCategories.stream()
                            .filter(cat -> cat.getId().equals(catId))
                            .findFirst()
                            .orElse(null));
                    }

                    Long accId = rs.getLong(7);
                    if (!rs.wasNull()) {
                        t.setAccount(allAccounts.stream()
                            .filter(acc -> acc.getId().equals(accId))
                            .findFirst()
                            .orElse(null));
                    }

                    User u = new User();
                    u.setId(rs.getLong(8));
                    t.setUser(u);
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Помилка БД при отриманні транзакцій за діапазоном: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> findByUserId(Long userId){

        var list = new ArrayList<Transaction>();

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
                        t.setCategory(allCategories.stream()
                            .filter(cat -> cat.getId().equals(catId))
                            .findFirst()
                            .orElse(null));
                    }

                    Long accId = rs.getLong(7);
                    if (!rs.wasNull()) {
                           t.setAccount(allAccounts.stream()
                            .filter(acc -> acc.getId().equals(accId))
                            .findFirst()
                            .orElse(null));
                    }

                    User u = new User();
                    u.setId(rs.getLong(8));
                    t.setUser(u);
                    list.add(t);
                }
            }
        } catch(SQLException e){ e.printStackTrace(); }
        return list;
    }


    @Override
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


            if(tx.getAccount()!=null && tx.getUser()!=null){

                Account acc = accountDao.findById(tx.getAccount().getId(), tx.getUser().getId());
                if (acc != null) {
                    double bal = acc.getBalance()==null?0.0:acc.getBalance();
                    if("EXPENSE".equalsIgnoreCase(tx.getType())) bal -= tx.getAmount();
                    else bal += tx.getAmount();
                    acc.setBalance(bal);

                    acc.setUser(tx.getUser());
                    accountDao.update(acc);
                }
            }
            return tx;
        } catch(SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Помилка БД при збереженні транзакції: " + e.getMessage());
        }
    }
}