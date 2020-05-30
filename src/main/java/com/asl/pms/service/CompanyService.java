package com.asl.pms.service;

import com.asl.pms.exception.NotFoundException;
import com.asl.pms.model.Company;
import com.asl.pms.model.Search;
import com.asl.pms.repository.CompanyRepository;
import com.asl.pms.viewmodels.ICompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CompanyService {

    private final CompanyRepository companyRepo;

    @Autowired
    public CompanyService(CompanyRepository companyRepo) {
        this.companyRepo = companyRepo;
    }

    public Company findCompany(long id) {
        Optional<Company> generic = companyRepo.findById(id);
        if (!generic.isPresent())
            throw new NotFoundException(Company.class);

        return generic.get();
    }

    public Iterable<Company> save(Set<Company> companies) {
        return companyRepo.saveAll(companies);
    }

    public List<ICompany> search(Search search) {
        String query = search.getQuery();
        return companyRepo.findTop50ByNameStartingWith(query);
    }
    
    public List<ICompany> findAll() {
		return companyRepo.findAllByOrderByNameAsc();
	}
}
