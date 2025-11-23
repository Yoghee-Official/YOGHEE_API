package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.Layout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LayoutRepository extends JpaRepository<Layout, String> {
    List<Layout> findAllByTypeOrderByOrderAsc(String type);
}
