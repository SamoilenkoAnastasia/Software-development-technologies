/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kpi.pa.data.repository;

import java.util.List;
import java.util.Optional;

/**
 * Загальний інтерфейс репозиторію (шаблон Repository)
 * @param <T> Тип сутності (наприклад, Transaction)
 * @param <ID> Тип ідентифікатора (наприклад, Long)
 */
public interface IRepository<T, ID> {
    
    T save(T entity);
    
    Optional<T> findById(ID id);
    
    List<T> findAll();
    
    void deleteById(ID id);
    
    // Додаткові операції можна додати тут
}