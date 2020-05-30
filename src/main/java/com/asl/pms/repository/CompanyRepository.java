package com.asl.pms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.Company;
import com.asl.pms.viewmodels.ICompany;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);

    List<ICompany> findTop50ByNameStartingWith(String name);
    
    List<ICompany> findAllByOrderByNameAsc();
}
