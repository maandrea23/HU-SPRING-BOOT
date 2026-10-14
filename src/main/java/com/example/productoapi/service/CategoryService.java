package com.example.productoapi.service;

import com.example.productoapi.model.Category;
import com.example.productoapi.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> listar() {
        return repository.findAll(org.springframework.data.domain.Sort.by("nombre"));
    }
}
