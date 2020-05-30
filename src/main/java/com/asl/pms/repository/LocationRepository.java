package com.asl.pms.repository;

import com.asl.pms.model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {
    Optional<Location> findByShelfAndRack(String shelf, int rack);
}
