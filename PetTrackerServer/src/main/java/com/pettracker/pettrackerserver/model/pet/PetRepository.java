package com.pettracker.pettrackerserver.model.pet;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PetRepository extends CrudRepository<Pet, Long>, JpaRepository<Pet, Long> {
	@Query(value = "SELECT * FROM pet WHERE fk_user_id=?1", nativeQuery = true)
	Collection<Pet> getAllPetsForUser(Long user_id);
	
	@Query(value = "SELECT * FROM pet WHERE fk_device_id=?1", nativeQuery = true)
	Optional<Pet> getPetByDevice(Long device_id);
	
	void deleteById(Long pet_id);

	Optional<Pet> findByName(String name);
	
	Optional<Pet> findById(Long id);
}
