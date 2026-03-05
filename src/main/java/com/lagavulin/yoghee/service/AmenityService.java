package com.lagavulin.yoghee.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lagavulin.yoghee.entity.Amenity;
import com.lagavulin.yoghee.model.dto.CodeInfoDto;
import com.lagavulin.yoghee.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public Map<String, List<CodeInfoDto>> getAllAmenities() {
        return amenityRepository.findAllOrderByIdNumeric()
                                .stream()
                                .filter(a -> a.getType() != null)
                                .collect(Collectors.groupingBy(
                                    Amenity::getType,
                                    Collectors.mapping(
                                        a -> new CodeInfoDto(a.getAmenityId(), a.getName()),
                                        Collectors.toList()
                                    )
                                ));
    }
}

