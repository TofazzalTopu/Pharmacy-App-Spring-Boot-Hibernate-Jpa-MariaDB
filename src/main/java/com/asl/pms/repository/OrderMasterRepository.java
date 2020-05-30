package com.asl.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.OrderMaster;

@Repository
public interface OrderMasterRepository extends JpaRepository<OrderMaster, Long> {

	 public List<OrderMaster> findAllByOrderByIdDesc();
    
}
