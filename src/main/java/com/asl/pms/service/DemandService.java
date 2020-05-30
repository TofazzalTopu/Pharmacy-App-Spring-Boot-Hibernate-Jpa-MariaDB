package com.asl.pms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.asl.pms.model.Demand;
import com.asl.pms.model.Drug;
import com.asl.pms.repository.DemandRepository;
import com.asl.pms.repository.DrugRepository;

@Service
public class DemandService {


    @Autowired
    private DrugRepository drugRepo;

    @Autowired
    private DemandRepository demandRepository;


    public Demand createDemand(Demand demand) {
        Demand savedDemand = demandRepository.save(demand);
        return savedDemand;
    }

    public boolean createDemandList(List<Demand> demandList) {
        demandList.forEach(demand ->
                demandRepository.save(demand)
        );
        return true;
    }

    public List<Demand> demandList() {
        List<Demand> demandList = demandRepository.findAll(sortByIdDesc());
        return demandList;
    }

    public Demand findById(Long id) {
        Demand demand = demandRepository.findById(id).get();
        return demand;
    }

    private Sort sortByIdDesc() {
        return new Sort(Sort.Direction.DESC, "id");
    }

    public List<Demand> getDemandListByIdAndPriority(String drugName, String priority) {
        List<Long> ids = new ArrayList<>();
        List<Drug> drugList = new ArrayList<>();
        List<Demand> demandList = new ArrayList<>();

        if (drugName != null && !drugName.equals("") && priority != null && !priority.equals("")) {
            drugList = drugRepo.findDrugsByNameStartingWith(drugName);
            if (drugList.size() > 0) {
                drugList.forEach(drug ->
                        ids.add(drug.getId())
                );
                if (priority != null && !priority.equals("")) {
                    demandList = demandRepository.findAllByDrugIdInAndPriorityEquals(ids, priority);
                }else {
                    demandList = demandRepository.findAllByDrugIdIn(ids);
                }
            }
        } else {
            demandList = demandRepository.findAllByPriorityEquals(priority);
        }

        return demandList;
    }
}
