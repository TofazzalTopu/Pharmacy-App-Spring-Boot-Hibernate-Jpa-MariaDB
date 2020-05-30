package com.asl.pms.repository;

import com.asl.pms.model.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {

    public List<Demand> findAllByPriorityEqualsAndCreateDate(String priority, Date date);

    public List<Demand> findAllByPriority(String priority);

    public List<Demand> findAllByDrugIdInAndPriorityEquals(List<Long> drugIds, String priority);

    public List<Demand> findAllByDrugIdIn(List<Long> drugIds);

    public List<Demand> findAllByPriorityEquals(String priority);

    public List<Demand> findAllByDrug_Name(String name);



}
