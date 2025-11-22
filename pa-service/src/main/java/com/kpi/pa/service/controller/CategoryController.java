package com.kpi.pa.service.controller;

import com.kpi.pa.service.dto.CategoryRequest;
import com.kpi.pa.service.model.Category;
import com.kpi.pa.service.repo.CategoryRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryRepo categoryRepo;

    public CategoryController(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest req) {
        Category c = new Category();
        c.setName(req.getName());
        c.setType(req.getType());
        c.setParentId(req.getParentId());
        c.setUserId(1L); 

        return ResponseEntity.ok(categoryRepo.save(c));
    }

    @GetMapping
    public List<Category> getCategories() {
        return categoryRepo.findAll();
    }
}
