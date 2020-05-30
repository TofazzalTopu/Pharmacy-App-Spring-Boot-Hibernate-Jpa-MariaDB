package com.asl.pms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asl.pms.model.Shelf;
import com.asl.pms.repository.ShelfRepository;

@Service
public class ShelfService {
	@Autowired
	ShelfRepository shelfRepo;

	public Shelf getOne(Long id) {
		return shelfRepo.findById(id).get();
	}

	public List<Shelf> findAll() {

		List<Shelf> shelfs =  shelfRepo.findAll();
		return shelfs;
	}

	public void saveRole(Shelf shelf) {
		shelfRepo.save(shelf);
	}
}
