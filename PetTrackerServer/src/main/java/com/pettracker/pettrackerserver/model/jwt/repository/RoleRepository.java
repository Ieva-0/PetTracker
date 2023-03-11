package com.pettracker.pettrackerserver.model.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettracker.pettrackerserver.model.jwt.models.ERole;
import com.pettracker.pettrackerserver.model.jwt.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}
