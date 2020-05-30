package com.asl.pms.service;

import com.asl.pms.exception.NotFoundException;
import com.asl.pms.model.Generic;
import com.asl.pms.model.Search;
import com.asl.pms.repository.GenericRepository;

import com.asl.pms.viewmodels.IGeneric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GenericService {

    private final GenericRepository genericRepo;

    @Autowired
    public GenericService(GenericRepository genericRepo) {
        this.genericRepo = genericRepo;
    }

    public Generic findGeneric(long id) {
        Optional<Generic> generic = genericRepo.findById(id);
        if (!generic.isPresent())
            throw new NotFoundException(Generic.class);

        return generic.get();
    }

    public Iterable<Generic> save(Set<Generic> generics) {
        return genericRepo.saveAll(generics);
    }
    public List<IGeneric> findAll() {
    	 return genericRepo.findAllByOrderByNameAsc();
    }

    public List<IGeneric> search(Search search) {
        String query = search.getQuery();
        /*if (query.isEmpty()) {
            return genericRepo.findAll();
        }*/

        return genericRepo.findTop50ByNameStartingWith(query);
    }
}
