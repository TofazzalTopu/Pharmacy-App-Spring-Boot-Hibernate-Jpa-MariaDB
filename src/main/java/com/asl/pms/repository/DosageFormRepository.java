package com.asl.pms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.DosageForm;
import com.asl.pms.viewmodels.IDosageForm;

@Repository
public interface DosageFormRepository extends CrudRepository<DosageForm, Long> {
    Optional<DosageForm> findByName(String name);

    List<IDosageForm> findTop50ByNameStartingWith(String name);
    
    List<IDosageForm> findAllByOrderByNameAsc();
}
