package com.lagavulin.yoghee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.lagavulin.yoghee.entity.UserCategory;
import com.lagavulin.yoghee.entity.UserFavorite;
import com.lagavulin.yoghee.entity.YogaCenter;
import com.lagavulin.yoghee.entity.YogaCenterAmenity;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.NewCenterDto;
import com.lagavulin.yoghee.model.dto.YogaCenterDto;
import com.lagavulin.yoghee.model.enums.TargetType;
import com.lagavulin.yoghee.model.swagger.main.center.CenterBaseDto;
import com.lagavulin.yoghee.repository.UserCategoryRepository;
import com.lagavulin.yoghee.repository.UserFavoriteRepository;
import com.lagavulin.yoghee.repository.YogaCenterAmenityRepository;
import com.lagavulin.yoghee.repository.YogaCenterRepository;
import com.lagavulin.yoghee.service.kakao.KakaoLocalService;
import com.lagavulin.yoghee.util.AddressParser;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class CenterService {

    private final YogaCenterRepository yogaCenterRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final YogaCenterAmenityRepository yogaCenterAmenityRepository;
    private final KakaoLocalService kakaoLocalService;

    public List<YogaCenterDto> getNewSignUpTopNCenterSinceStartDate(String type, int n, String userUuid) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        Set<String> centerIds = new HashSet<>(yogaCenterRepository.findNewSignUpTopNClassSinceStartDate(type, oneWeekAgo, PageRequest.of(0, n)));
        return yogaCenterRepository.findYogaCenterWithFavoriteCount(centerIds, userUuid);
    }

    public List<YogaCenterDto> getUserCategoryNCenter(String userUuid, int n) {
        List<String> categoryIds = userCategoryRepository.findAllByUserUuid(userUuid)
                                                         .stream()
                                                         .map(UserCategory::getCategoryId)
                                                         .toList();

        Set<String> centerIds = new HashSet<>(yogaCenterRepository.findRandomByCategoryIds(categoryIds, PageRequest.of(0, n))
                                                                  .stream()
                                                                  .map(YogaCenter::getCenterId)
                                                                  .toList());
        if (centerIds.size() < n) {
            int remaining = n - centerIds.size();
            centerIds.addAll(yogaCenterRepository.findRandomNCenter(PageRequest.of(0, remaining)));
        }

        return yogaCenterRepository.findWithReviewStatsByCenterIds(centerIds, userUuid);
    }

    public List<YogaCenterDto> getClickedTopNCenterLastMDays(int n, int day) {
        LocalDate fromDate = LocalDate.now().minusDays(day);
        Set<String> centerIds = new HashSet<>(yogaCenterRepository.findTopClickedClasses(fromDate, PageRequest.of(0, n)));
        if (centerIds.size() < n) {
            int remaining = n - centerIds.size();
            centerIds.addAll(yogaCenterRepository.findRandomNCenter(PageRequest.of(0, remaining)));
        }
        return yogaCenterRepository.findWithReviewStatsByCenterIds(centerIds, null);
    }

    public void addFavoriteClass(String userUuid, String centerId) {
        Optional<UserFavorite> userFavorite = userFavoriteRepository.findById(new UserFavorite.PK(centerId, "CENTER", userUuid));

        if (userFavorite.isPresent()) {
            userFavoriteRepository.delete(userFavorite.get());
        } else {
            userFavoriteRepository.save(UserFavorite.builder()
                                                    .id(centerId)
                                                    .type(TargetType.CENTER)
                                                    .userUuid(userUuid)
                                                    .createdAt(new Date())
                                                    .build());
        }
    }

    public List<CenterBaseDto> findCenterByUserUuid(String userUuid) {
        return yogaCenterRepository.findByMasterId(userUuid).stream()
                                   .map(center -> CenterBaseDto.builder()
                                                               .centerId(center.getCenterId())
                                                               .name(center.getName())
                                                               .address(center.getFullAddress())
                                                               .createdAt(center.getCreatedAt())
                                                               .build())
                                   .toList();
    }

    public void deleteCenter(String centerId, String userUuid) {
        YogaCenter center = yogaCenterRepository.findById(centerId)
                                                .orElseThrow(() -> new BusinessException(ErrorCode.CENTER_NOT_FOUND, "해당 요가원을 찾을 수 없습니다."));

        if (!center.getMasterId().equals(userUuid)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인이 등록한 요가원만 삭제할 수 있습니다.");
        }

        yogaCenterRepository.delete(center);
    }

    public YogaCenterDto findCenterById(String centerId, String userUuid) {
        // 단일 요가원 조회를 위해 기존 쿼리 메소드 재사용
        Set<String> centerIds = Set.of(centerId);
        List<YogaCenterDto> results = yogaCenterRepository.findWithReviewStatsByCenterIds(centerIds, userUuid);

        if (results.isEmpty()) {
            return null;
        }

        YogaCenterDto center = results.get(0);
        // amenity 정보 추가
        center.setAmenityIds(yogaCenterAmenityRepository.findAmenityIdsByCenterId(centerId));

        return center;
    }

    public void createCenter(String userUuid, NewCenterDto dto) {
        YogaCenter yogaCenter = YogaCenter.builder()
                                          .masterId(userUuid)
                                          .createdAt(new Date())
                                          .build();
        applyDtoToCenter(yogaCenter, dto);
        yogaCenterRepository.save(yogaCenter);
        saveAmenities(yogaCenter.getCenterId(), dto);
    }

    public void updateCenter(String userUuid, NewCenterDto dto) {
        YogaCenter yogaCenter = yogaCenterRepository.findById(dto.getCenterId())
                                                    .orElseThrow(() -> new BusinessException(ErrorCode.CENTER_NOT_FOUND, "해당 요가원을 찾을 수 없습니다."));

        if (!yogaCenter.getMasterId().equals(userUuid)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인이 등록한 요가원만 수정할 수 있습니다.");
        }

        yogaCenterAmenityRepository.deleteByCenterId(dto.getCenterId());
        applyDtoToCenter(yogaCenter, dto);
        yogaCenterRepository.save(yogaCenter);
        saveAmenities(dto.getCenterId(), dto);
    }

    private void applyDtoToCenter(YogaCenter yogaCenter, NewCenterDto dto) {
        String addressToParse = StringUtils.hasText(dto.getJibunAddress())
            ? dto.getJibunAddress()
            : dto.getRoadAddress();
        AddressParser.ParsedAddress parsed = AddressParser.parse(addressToParse);

        yogaCenter.setName(dto.getName());
        yogaCenter.setDescription(dto.getDescription());
        yogaCenter.setThumbnail(dto.getThumbnail());
        yogaCenter.setDepth1(parsed.getDepth1());
        yogaCenter.setDepth2(parsed.getDepth2());
        yogaCenter.setDepth3(parsed.getDepth3());
        yogaCenter.setRoadAddress(dto.getRoadAddress());
        yogaCenter.setJibunAddress(dto.getJibunAddress());
        yogaCenter.setZonecode(dto.getZonecode());
        yogaCenter.setAddressDetail(dto.getAddressDetail());

        String baseAddress = StringUtils.hasText(dto.getRoadAddress())
            ? dto.getRoadAddress()
            : dto.getJibunAddress();
        yogaCenter.setFullAddress(StringUtils.hasText(dto.getAddressDetail())
            ? baseAddress + " " + dto.getAddressDetail()
            : baseAddress);

        double[] coordinates = kakaoLocalService.getCoordinatesFromAddresses(
            dto.getRoadAddress(), dto.getJibunAddress());
        if (coordinates != null) {
            yogaCenter.setLatitude(coordinates[0]);
            yogaCenter.setLongitude(coordinates[1]);
        }
    }

    private void saveAmenities(String centerId, NewCenterDto dto) {
        if (dto.getAmenityIds() == null || dto.getAmenityIds().isEmpty()) {
            return;
        }
        yogaCenterAmenityRepository.saveAll(
            dto.getAmenityIds().stream()
               .map(amenityId -> YogaCenterAmenity.builder()
                                                  .centerId(centerId)
                                                  .amenityId(amenityId)
                                                  .build())
               .toList());
    }
}