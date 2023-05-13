package com.pettracker.pettrackerserver.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pettracker.pettrackerserver.users.models.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer>, JpaRepository<Role, Integer> {
	Optional<Role> findByName(String name);
	Optional<Role> findById(Integer id);

}
