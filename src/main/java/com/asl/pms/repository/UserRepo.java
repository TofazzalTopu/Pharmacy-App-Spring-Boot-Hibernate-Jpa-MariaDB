package com.asl.pms.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.asl.pms.model.Role;
import com.asl.pms.model.User;
import com.asl.pms.viewmodels.ICustomer;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {
	public User findByUsername(String username);
	public User findByEmail(String email);
	public List<User> findByActive(boolean active);
	
	
	public List<ICustomer> findByEmailStartingWith(String email);
	public List<User> findByRolesAndEmailStartingWith(Role role, String email);
}
