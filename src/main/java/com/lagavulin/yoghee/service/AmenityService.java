package com.lagavulin.yoghee.service;

import java.util.List;
import java.util.stream.Collectors;

import com.lagavulin.yoghee.model.dto.CodeInfoDto;
import com.lagavulin.yoghee.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public List<CodeInfoDto> getAllAmenities() {
        return amenityRepository.findAll()
                                .stream()
                                .map(a -> new CodeInfoDto(a.getAmenityId(), a.getName()))
                                .collect(Collectors.toList());
    }
}

