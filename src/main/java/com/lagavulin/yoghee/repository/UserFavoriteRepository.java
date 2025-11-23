package com.lagavulin.yoghee.repository;

import com.lagavulin.yoghee.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFavoriteRepository  extends JpaRepository<UserFavorite, UserFavorite.PK> {

}