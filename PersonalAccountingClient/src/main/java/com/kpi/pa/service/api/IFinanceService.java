/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kpi.pa.service.api;

import com.kpi.pa.service.dto.TransactionDTO;

import java.util.List;

public interface IFinanceService {
    /**
     * Зберігає нову транзакцію та оновлює баланс рахунку.
     * @param dto Об'єкт передачі даних транзакції.
     * @return true, якщо збереження успішне.
     */
    boolean saveTransaction(TransactionDTO dto);

    // Інші методи, які можуть знадобитися
    List<String> getAllAccountNames();
    List<String> getAllCategoryNames();
}