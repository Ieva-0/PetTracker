package com.pettracker.pettrackerserver.users.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.pettracker.pettrackerserver.users.models.User;
import com.pettracker.pettrackerserver.users.repository.UserRepository;

@Service
public class UserDao {
	@Autowired
	private UserRepository repository;
	
	public List<User> allUsers() {
		List<User> users = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(users::add);
		return users;
	}
	
	public Optional<User> getByUsername(String username) {
		return repository.findByUsername(username);
	}
	
	public Optional<User> getByEmail(String email) {
		return repository.findByEmail(email);
	}
	
	public Optional<User> getById(Long id) {
		return repository.findById(id);
	}
	
	public boolean existsByUsername(String username) {
		return repository.existsByUsername(username);
	}
	
	public boolean existsByEmail(String email) {
		return repository.existsByEmail(email);
	}
	
	public User save(User user) {
		return repository.save(user);
	}
	
	public void deleteById(Long user_id) {
		repository.deleteById(user_id);
	}
	
}
