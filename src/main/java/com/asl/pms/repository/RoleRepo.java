package com.asl.pms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.Role;

@Repository
public interface RoleRepo extends CrudRepository<Role, Long> {
}
