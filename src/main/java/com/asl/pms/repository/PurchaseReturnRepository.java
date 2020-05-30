package com.asl.pms.repository;

import com.asl.pms.model.PurchaseMaster;
import com.asl.pms.model.PurchaseReturn;
import com.asl.pms.model.PurchaseReturnDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseReturnRepository extends JpaRepository<PurchaseReturn, Long> {
    List<PurchaseReturn> findAllByPurchaseMasterIn(List<PurchaseMaster> masterList);

}
