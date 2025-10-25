/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kpi.pa.data.repository.impl;

import com.kpi.pa.data.DbConfig;
import com.kpi.pa.data.repository.IRepository;
import com.kpi.pa.domain.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransactionRepositoryImpl implements IRepository<Transaction, Long> {

    // ---------------------- ГОЛОВНА ЛОГІКА ЗБЕРЕЖЕННЯ ----------------------
    @Override
    public Transaction save(Transaction transaction) {
        // Тимчасова реалізація для усунення помилок компіляції та підтвердження логіки.
        // Реальний SQL-код буде додано пізніше.
        
        System.out.println("Repository: Спроба збереження транзакції: " + transaction.getDescription());
        
        // Тут має бути логіка:
        // 1. Отримати account_id та category_id з БД по їх назвах (якщо використовуємо назви)
        // 2. Підготувати SQL INSERT INTO transactions (...) VALUES (...)
        
        try (Connection connection = DbConfig.getConnection()) {
            // ... (повноцінний JDBC-код) ...
            System.out.println("Repository: Імітація успішного збереження в БД.");
        } catch (SQLException e) {
            System.err.println("Repository: Помилка SQL при збереженні: " + e.getMessage());
            // Обробка помилок
        }
        return transaction;
    }
    // --------------------------------------------------------------------------

    @Override
    public Optional<Transaction> findById(Long id) {
        return Optional.empty(); // Заглушка
    }

    @Override
    public List<Transaction> findAll() {
        return List.of(); // Заглушка
    }

    @Override
    public void deleteById(Long id) {
        // Заглушка
    }
}