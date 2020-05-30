package com.asl.pms.repository;

import com.asl.pms.model.Generic;
import com.asl.pms.viewmodels.IGeneric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenericRepository extends JpaRepository<Generic, Long> {
    Optional<Generic> findByName(String name);

    List<IGeneric> findTop50ByNameStartingWith(String name);
    
    List<Generic> findTop50ByNameContaining(String name);
    
    //List<IGeneric> findAllOrderByNameAsc();
    
    List<IGeneric> findAllByOrderByNameAsc();
    
}
