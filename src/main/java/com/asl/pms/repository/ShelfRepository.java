package com.asl.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.Shelf;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

   
}
