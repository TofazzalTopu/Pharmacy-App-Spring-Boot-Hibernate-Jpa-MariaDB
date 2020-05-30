package com.asl.pms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asl.pms.model.Rack;
import com.asl.pms.model.Shelf;
import com.asl.pms.repository.RackRepository;
import com.asl.pms.repository.ShelfRepository;

@Service
public class RackService {
	@Autowired
	RackRepository rackRepo;
	
	
	 @Autowired 
	 ShelfRepository shelfRepo;
	 

	public Rack getOne(Long id) {
		return rackRepo.findById(id).get();
	}

	public List<Rack> findAll() {

		List<Rack> racks =  rackRepo.findAll();
		return racks;
	}

	public void saveRole(Rack rack) {
		rackRepo.save(rack);
	}
	
	public List<Rack> findRackByShelfId(Long id) {
		Shelf shelf=shelfRepo.findById(id).get();
		List<Rack> racks =  rackRepo.findRackByShelf(shelf);
		return racks;
	}
	
}
