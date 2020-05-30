package com.asl.pms.repository;

import com.asl.pms.model.PurchaseReturn;
import com.asl.pms.model.PurchaseReturnDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseReturnDetailsRepository extends JpaRepository<PurchaseReturnDetails, Long> {
    List<PurchaseReturnDetails> findByPurchaseReturn(PurchaseReturn purchaseReturn);

    
}
