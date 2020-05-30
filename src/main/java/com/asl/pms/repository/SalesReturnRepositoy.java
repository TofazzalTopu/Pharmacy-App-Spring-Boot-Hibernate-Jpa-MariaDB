package com.asl.pms.repository;


import com.asl.pms.model.SalesReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesReturnRepositoy extends JpaRepository<SalesReturn, Long> {
}
