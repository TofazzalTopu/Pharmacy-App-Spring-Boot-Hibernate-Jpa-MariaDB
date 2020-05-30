package com.asl.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.asl.pms.model.SalesDetails;
import com.asl.pms.model.SalesMaster;

@Repository
public interface SalesDetailsRepository extends JpaRepository<SalesDetails, Long> {

	
	
	List<SalesDetails> findBySalesMaster(SalesMaster salesMaster);
	
	
	
	List<SalesDetails> findBySalesMasterId(Long id);
	
    

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "delete FROM SalesDetails WHERE sales_master_id=:id")
	int deleteSalesDetailsByMstId(@Param("id") Long id);
	
	
}
