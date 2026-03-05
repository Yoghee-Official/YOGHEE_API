package com.lagavulin.yoghee.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lagavulin.yoghee.entity.Category;
import com.lagavulin.yoghee.model.dto.CodeInfoDto;
import com.lagavulin.yoghee.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Map<String, List<CodeInfoDto>> getAllCategories() {
        return categoryRepository.findAllOrderByIdNumeric()
                                 .stream()
                                 .filter(c -> c.getType() != null)
                                 .collect(Collectors.groupingBy(
                                     Category::getType,
                                     Collectors.mapping(
                                         c -> new CodeInfoDto(c.getCategoryId(), c.getName()),
                                         Collectors.toList()
                                     )
                                 ));
    }
}
