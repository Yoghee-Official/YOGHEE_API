package com.lagavulin.yoghee.repository;

import com.lagavulin.yoghee.entity.ClassPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassPolicyRepository extends JpaRepository<ClassPolicy, String> {

}

