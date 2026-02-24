package com.lagavulin.yoghee.service;

import java.util.List;
import java.util.stream.Collectors;

import com.lagavulin.yoghee.model.dto.CodeInfoDto;
import com.lagavulin.yoghee.repository.FeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureService {

    private final FeatureRepository featureRepository;

    public List<CodeInfoDto> getAllFeatures() {
        return featureRepository.findAll()
                                .stream()
                                .map(f -> new CodeInfoDto(String.valueOf(f.getId()), f.getDescription()))
                                .collect(Collectors.toList());
    }
}

