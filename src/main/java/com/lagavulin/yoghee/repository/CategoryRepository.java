package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query(value = """
        SELECT new Category(c.categoryId, c.name, c.description)
        FROM Category c
        JOIN YogaClassCategory ycc ON c.categoryId = ycc.categoryId
        JOIN YogaClass yc ON yc.classId = ycc.classId
        WHERE yc.type = :type
        ORDER BY RAND()
    """)
    List<Category> findRandomCategoriesWithClass(String type, Pageable pageable);

    List<Category> findAllByTypeAndMainDisplayEquals(String type, String mainDisplay);
}
