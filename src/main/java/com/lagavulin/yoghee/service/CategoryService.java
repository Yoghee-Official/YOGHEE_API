package com.lagavulin.yoghee.service;

import java.util.List;

import com.lagavulin.yoghee.entity.Category;
import com.lagavulin.yoghee.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Main
     */
    public List<Category> getRandomNCategoriesWithClass(String type, int n) {
        return categoryRepository.findRandomCategoriesWithClass(type, PageRequest.of(0, n));
    }

    public List<Category> getMainDisplay(String type) {
        return categoryRepository.findAllByTypeAndMainDisplayEquals(type, "Y");
    }
}
