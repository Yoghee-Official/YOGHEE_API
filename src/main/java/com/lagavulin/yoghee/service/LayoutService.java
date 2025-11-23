package com.lagavulin.yoghee.service;

import java.util.List;

import com.lagavulin.yoghee.entity.Layout;
import com.lagavulin.yoghee.repository.LayoutRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LayoutService {
    private final LayoutRepository layoutRepository;

    public List<Layout> getMainLayouts(String type){
        return layoutRepository.findAllByTypeOrderByOrderAsc(type);
    }
}
