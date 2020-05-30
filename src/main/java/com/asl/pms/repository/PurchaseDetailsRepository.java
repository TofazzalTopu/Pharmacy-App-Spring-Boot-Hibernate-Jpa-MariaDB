package com.asl.pms.repository;

import java.util.List;

import com.asl.pms.model.PurchaseReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.asl.pms.model.PurchaseDetails;
import com.asl.pms.model.PurchaseMaster;

@Repository
public interface PurchaseDetailsRepository extends JpaRepository<PurchaseDetails, Long> {

	List<PurchaseDetails> findByPurchaseMaster(PurchaseMaster purchasemaster);
	
	@Query(value = "SELECT m.id, t.name, i.product_name, d.notes, d.quantity, d.id AS order_id FROM purchase_master m, order_details d, s_tables t, s_inventory_master i WHERE d.s_order_master_id=m.id AND m.table_id=t.id AND d.menu_items_id=i.id AND d.order_status=\"CREATED\"", nativeQuery = true)
    List<PurchaseDetails> findByPurchaseDetailsInfo();
	
	List<PurchaseDetails> findByPurchaseMasterId(Long id);
	
    

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "delete FROM PurchaseDetails WHERE purchase_master_id=:id")
	int deletePurchaseDetailsByMstId(@Param("id") Long id);

}
