package com.asl.pms.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.PurchaseMaster;

@Repository
public interface PurchaseMasterRepository extends JpaRepository<PurchaseMaster, Long> {

	public List<PurchaseMaster> findAllByReturnFlag(String returnFlag);

	@Query(value = "SELECT * FROM purchase_master purchase WHERE inv_date  BETWEEN ?1 AND ?2", nativeQuery = true)
	public List<PurchaseMaster> purchaseListByDateRange(String startDate, String endDate);
	
	public List<PurchaseMaster> findAllByCreateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
	
	public List<PurchaseMaster> findAllByInvDateBetween(String startDate, String endDate);


}
