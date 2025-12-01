package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.YogaCenterAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface YogaCenterAddressRepository extends JpaRepository<YogaCenterAddress, String> {

    @Query("""
        SELECT a
        FROM YogaCenterAddress a
        WHERE a.userUuid = :userUuid
        """)
    List<YogaCenterAddress> findAddressByUserUuid(String userUuid);

    @Query("""
            SELECT a
            FROM YogaCenterAddress a
            WHERE lower(a.streetAddress) LIKE lower(concat('%', :keyword, '%'))
        """)
    List<YogaCenterAddress> searchByKeyword(String keyword);
}