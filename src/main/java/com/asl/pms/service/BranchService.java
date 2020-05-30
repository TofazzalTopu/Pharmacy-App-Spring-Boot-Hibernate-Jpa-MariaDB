package com.asl.pms.service;

import com.asl.pms.model.Branch;
import com.asl.pms.repository.BrancRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    @Autowired
    BrancRepository brancRepository;

    public List<Branch> getAll(){
        return  (List<Branch>) brancRepository.findAll();
    }

    public Branch findOne(Long id){
        return brancRepository.findById(id).get();
    }

    public void saveBranchs(Branch branch){
        brancRepository.save(branch);
    }
}
