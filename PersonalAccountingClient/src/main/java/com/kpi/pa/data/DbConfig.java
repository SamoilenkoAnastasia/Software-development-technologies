/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kpi.pa.data;

/**
 *
 * @author ANAST
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class DbConfig {

    // Переконайтеся, що ці константи відповідають вашій БД
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/personal_finance";
    private static final String USER = "root"; 
    private static final String PASSWORD = "N_030306-a"; // <--- Встановіть ваш пароль
    
    // ... (статичний блок залишається без змін)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Помилка: Не знайдено JDBC-драйвер MySQL.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * НОВИЙ МЕТОД: Перевіряє встановлення з'єднання з БД
     */
    public static boolean testConnection() {
        System.out.println("Спроба підключення до БД: " + URL);
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Успішне підключення до бази даних personal_finance!");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ ПОМИЛКА ПІДКЛЮЧЕННЯ ДО БАЗИ ДАНИХ!");
            System.err.println("Код помилки SQL: " + e.getSQLState());
            System.err.println("Повідомлення: " + e.getMessage());
            return false;
        }
    }
}