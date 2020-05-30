package com.asl.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.TnxMaster;
import com.asl.pms.viewmodels.DrugStock;
@Repository
public interface TnxMasterRepository extends JpaRepository<TnxMaster, Long> {

	
	
	@Query(value = "SELECT d.id,  d.total_qty stock, m.tnx_mode strength, m.tnx_type company, m.create_time generic    \r\n" + 
			"	FROM tnx_details d,  tnx_master m WHERE  d.tnx_master_id=m.id  \r\n" + 
			"	AND  d.drug_id=?1 \r\n" + 
			"	ORDER BY m.create_time", nativeQuery = true)
	List<DrugStock> medicineInOutHistory(String drugId);
}
