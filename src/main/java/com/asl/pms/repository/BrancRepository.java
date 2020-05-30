package com.asl.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.Branch;

@Repository
public interface BrancRepository extends JpaRepository<Branch, Long> {
}
