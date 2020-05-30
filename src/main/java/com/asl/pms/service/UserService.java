package com.asl.pms.service;

import com.asl.pms.model.Role;
import com.asl.pms.model.User;
import com.asl.pms.repository.UserRepo;
import com.asl.pms.viewmodels.ICustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		UserDetails userd = (UserDetails) user;
		return userd;
	}

	public List<User> getAllUser() {
		return (List<User>) userRepo.findAll();
	}

	public User usernameExist(String email) {
		return userRepo.findByEmail(email);
	}

	@Transactional
	public void saveUser(User user) {
		userRepo.save(user);
	}

	public User findOne(Long id) {
		return userRepo.findById(id).get();
	}

	public List<User> findActiveUser(boolean active) {
		return userRepo.findByActive(active);
	}

	public User findByUserName(String username) {
		return userRepo.findByUsername(username);
	}

	public List<ICustomer> findByEmailStartingWith(String query) {
		return userRepo.findByEmailStartingWith(query);
	}
	public List<User> findByRolesAndEmailStartingWith(Role role, String email) {
		return userRepo.findByRolesAndEmailStartingWith(role, email);
	}

}
