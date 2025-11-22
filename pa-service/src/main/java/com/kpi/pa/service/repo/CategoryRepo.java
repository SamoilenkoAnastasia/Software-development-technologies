package com.kpi.pa.service.repo;

import com.kpi.pa.service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
}
