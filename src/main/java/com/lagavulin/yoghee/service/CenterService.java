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
import com.lagavulin.yoghee.entity.YogaCenterAddress;
import com.lagavulin.yoghee.model.dto.CenterAddressDto;
import com.lagavulin.yoghee.model.dto.YogaCenterDto;
import com.lagavulin.yoghee.model.enums.TargetType;
import com.lagavulin.yoghee.repository.UserCategoryRepository;
import com.lagavulin.yoghee.repository.UserFavoriteRepository;
import com.lagavulin.yoghee.repository.YogaCenterAddressRepository;
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
    private final YogaCenterAddressRepository yogaCenterAddressRepository;
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

    public List<YogaCenterAddress> findAddressByUserUuid(String name) {
        return yogaCenterAddressRepository.findAddressByUserUuid(name);
    }

    public List<YogaCenterAddress> searchByKeyword(String keyword) {
        return yogaCenterAddressRepository.searchByKeyword(keyword);
    }

    public void saveCenterAddress(String userUuid, CenterAddressDto dto) {
        YogaCenterAddress address;

        // addressId가 있으면 기존 주소 수정
        if (StringUtils.hasText(dto.getAddressId())) {
            address = yogaCenterAddressRepository.findById(dto.getAddressId())
                                                 .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주소입니다."));

            // 소유자 확인
            if (!address.getUserUuid().equals(userUuid)) {
                throw new IllegalArgumentException("해당 주소를 수정할 권한이 없습니다.");
            }
        } else {
            // 신규 등록
            address = YogaCenterAddress.builder()
                                       .userUuid(userUuid)
                                       .createdAt(new Date())
                                       .build();
        }

        AddressParser.ParsedAddress parsed = AddressParser.parse(dto.getJibunAddress());

        // 기본 정보 설정
        address.setDepth1(parsed.getDepth1());
        address.setDepth2(parsed.getDepth2());
        address.setDepth3(parsed.getDepth3());
        address.setRoadAddress(dto.getRoadAddress());
        address.setJibunAddress(dto.getJibunAddress());
        address.setZonecode(dto.getZonecode());
        address.setAddressDetail(dto.getAddressDetail());
        address.setFullAddress(dto.getRoadAddress() + " " + dto.getAddressDetail());
        address.setName(dto.getName());

        // 위도/경도 설정
        // 1. DTO에서 제공된 좌표 사용
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            address.setLatitude(dto.getLatitude());
            address.setLongitude(dto.getLongitude());
        } else {
            // 2. DTO에 좌표가 없으면 Kakao API로 주소 검색하여 좌표 얻기
            double[] coordinates = kakaoLocalService.getCoordinatesFromAddresses(
                dto.getRoadAddress(),
                dto.getJibunAddress()
            );

            if (coordinates != null) {
                address.setLatitude(coordinates[0]); // latitude
                address.setLongitude(coordinates[1]); // longitude
            }
            // 좌표를 가져오지 못한 경우 null로 저장됨
        }

        yogaCenterAddressRepository.save(address);
    }
}