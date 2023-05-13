package com.pettracker.pettrackerserver.users.controllers;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettracker.pettrackerserver.users.models.Role;

@RestController 
@RequestMapping("roles")
public class RoleController {
	
	@Autowired
	private RoleDao roledao;

	
	@GetMapping("all")
	public List<Role> allRoles(@RequestHeader("Authorization") String token) {
		return roledao.allRoles();
	}

	

}