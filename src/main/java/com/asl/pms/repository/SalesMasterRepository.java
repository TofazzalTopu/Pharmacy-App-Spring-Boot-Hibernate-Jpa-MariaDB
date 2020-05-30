package com.asl.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.SalesMaster;

@Repository
public interface SalesMasterRepository extends JpaRepository<SalesMaster, Long> {

	public List<SalesMaster> findAllBySalesDateBetween(String startDate, String endDate);
}
