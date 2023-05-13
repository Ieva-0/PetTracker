package com.pettracker.pettrackerserver.users.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.pettracker.pettrackerserver.users.models.Role;
import com.pettracker.pettrackerserver.users.repository.RoleRepository;


@Service
public class RoleDao {
	@Autowired
	private RoleRepository repository;
	
	public List<Role> allRoles() {
		List<Role> roles = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(roles::add);
		return roles;
	}
	public Optional<Role> findRoleById(Integer id) {
		return repository.findById(id);
	}
	
}
