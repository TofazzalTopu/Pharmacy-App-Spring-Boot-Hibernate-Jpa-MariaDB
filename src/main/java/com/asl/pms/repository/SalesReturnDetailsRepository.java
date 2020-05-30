package com.asl.pms.repository;

import com.asl.pms.model.SalesReturn;
import com.asl.pms.model.SalesReturnDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesReturnDetailsRepository extends JpaRepository<SalesReturnDetails, Long> {

    public List<SalesReturnDetails> findAllBySalesReturn(SalesReturn salesReturn);

}
