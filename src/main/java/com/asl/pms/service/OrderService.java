package com.asl.pms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.asl.pms.model.OrderDetails;
import com.asl.pms.model.OrderMaster;
import com.asl.pms.repository.OrderDetailsRepository;
import com.asl.pms.repository.OrderMasterRepository;

@Service
public class OrderService {
	@Autowired
	OrderMasterRepository orderMasterRepo;

	@Autowired
	OrderDetailsRepository orderDetailsRepo;

	public OrderMaster getOrderMasterById(Long id) {
		return orderMasterRepo.findById(id).get();
	}

	public OrderDetails getOrderDetailsById(Long id) {
		return orderDetailsRepo.findById(id).get();
	}

	public List<OrderMaster> findAllOrderMaster() {
		// return (List<OrderMaster>) orderMasterRepo.findAll();
		return (List<OrderMaster>) orderMasterRepo.findAll(sortByIdDesc());
	}

	public List<OrderDetails> findAllOrderDetails() {
		return (List<OrderDetails>) orderDetailsRepo.findAll();
	}

	public OrderMaster saveOrderMaster(OrderMaster orderMaster) {
		OrderMaster saved=orderMasterRepo.save(orderMaster);		
		return saved;
	}

	public OrderDetails saveOrderDetails(OrderDetails orderDetails) {
		OrderDetails saved=orderDetailsRepo.save(orderDetails);
		return saved;
	}
	
	public List<OrderDetails> findByOrderMaster(OrderMaster orderMaster) {
		return (List<OrderDetails>) orderDetailsRepo.findByOrderMaster(orderMaster);
	}
	
	private Sort sortByIdDesc() {
        return new Sort(Sort.Direction.DESC, "id");
    }
}
