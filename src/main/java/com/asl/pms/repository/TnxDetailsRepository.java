package com.asl.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.asl.pms.model.TnxDetails;
import com.asl.pms.model.TnxMaster;

@Repository
public interface TnxDetailsRepository extends JpaRepository<TnxDetails, Long> {

	
	
	List<TnxDetails> findByTnxMaster(TnxMaster tnxMaster);
	
	
	
	List<TnxDetails> findByTnxMasterId(Long id);
	
   

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "delete FROM TnxDetails WHERE tnx_master_id=:id")
	int deleteTnxDetailsByMstId(@Param("id") Long id);
	
	
}
