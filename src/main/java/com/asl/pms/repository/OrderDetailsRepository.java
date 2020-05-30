package com.asl.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.OrderDetails;
import com.asl.pms.model.OrderMaster;
import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

	
	
	List<OrderDetails> findByOrderMaster(OrderMaster ordermaster);
	
	@Query(value = "SELECT m.id, t.name, i.product_name, d.notes, d.quantity, d.id AS order_id FROM order_master m, order_details d, s_tables t, s_inventory_master i WHERE d.s_order_master_id=m.id AND m.table_id=t.id AND d.menu_items_id=i.id AND d.order_status=\"CREATED\"", nativeQuery = true)
    List<OrderDetails> findByOrderDetailsInfo();
	
}
