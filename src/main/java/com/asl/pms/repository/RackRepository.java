package com.asl.pms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.Rack;
import com.asl.pms.model.Shelf;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

	List<Rack> findRackByShelf(Shelf shelf);
	//List<Rack> findByShelfId(Long id);

}
