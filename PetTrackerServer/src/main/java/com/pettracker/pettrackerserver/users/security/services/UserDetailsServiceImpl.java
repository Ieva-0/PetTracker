package com.pettracker.pettrackerserver.users.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pettracker.pettrackerserver.users.models.Role;
import com.pettracker.pettrackerserver.users.models.User;
import com.pettracker.pettrackerserver.users.repository.RoleRepository;
import com.pettracker.pettrackerserver.users.repository.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		Role role = roleRepository.findById(user.getFk_role_id())
				.orElseThrow(() -> new RuntimeException("role not found with id: " + user.getFk_role_id()));
		return UserDetailsImpl.build(user, role);
	}

}