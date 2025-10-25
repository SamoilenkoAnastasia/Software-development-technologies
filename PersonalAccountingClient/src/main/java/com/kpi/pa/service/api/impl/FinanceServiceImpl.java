/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kpi.pa.service.api.impl;

/**
 *
 * @author ANAST
 */
import com.kpi.pa.data.repository.impl.TransactionRepositoryImpl;
import com.kpi.pa.domain.Transaction;
import com.kpi.pa.service.api.IFinanceService;
import com.kpi.pa.service.dto.TransactionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random; // Тимчасове рішення для заглушки ID

public class FinanceServiceImpl implements IFinanceService {

    private final TransactionRepositoryImpl transactionRepository;

    public FinanceServiceImpl() {
        this.transactionRepository = new TransactionRepositoryImpl();
    }

    @Override
    public boolean saveTransaction(TransactionDTO dto) {
        // 1. Перевірка даних (як зазначено на діаграмі послідовностей) [cite: 116]
        if (dto.getAmount() <= 0 || dto.getAccountName() == null || dto.getCategoryName() == null) {
            System.err.println("Помилка: Некоректні дані транзакції.");
            return false; // Повідомлення про помилку [cite: 124]
        }

        // 2. Створення об'єкта домену (Transaction) [cite: 117]
        try {
            // У реальному проєкті ID, account_id та category_id будуть отримані з БД
            Transaction transaction = new Transaction();
            transaction.setId(new Random().nextLong()); // Тимчасовий ID
            transaction.setAmount(dto.getAmount());
            transaction.setType(dto.getType()); // income/expense
            transaction.setDate(dto.getDate() != null ? dto.getDate() : LocalDateTime.now());
            transaction.setDescription(dto.getDescription());
            
            // 3. Збереження транзакції (через TransactionRepository) [cite: 124]
            transactionRepository.save(transaction);
            
            // 4. Оновлення балансу рахунку (це повинна робити окрема логіка в AccountRepository, 
            // але для простоти реалізуємо тут лише збереження транзакції) [cite: 118]
            System.out.println("✅ Транзакцію успішно додано. Потрібне оновлення балансу рахунку: " + dto.getAccountName());
            return true; // Підтвердження збереження
            
        } catch (Exception e) {
            System.err.println("❌ Помилка збереження даних у БД: " + e.getMessage()); // Виняток [cite: 123]
            return false;
        }
    }
    
    // Тимчасові заглушки для отримання даних для випадаючих списків (ComboBox)
    @Override
    public List<String> getAllAccountNames() {
        return List.of("Рахунок 1 (основний)", "Готівка", "Кредитна картка");
    }

    @Override
    public List<String> getAllCategoryNames() {
        return List.of("Зарплата", "Оренда", "Продукти", "Транспорт");
    }
}