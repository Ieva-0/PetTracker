package com.pettracker.pettrackerserver.pets;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface PetRepository extends CrudRepository<Pet, Long>, JpaRepository<Pet, Long> {
	@Query(value = "SELECT * FROM pet WHERE fk_user_id=?1", nativeQuery = true)
	Collection<Pet> getAllPetsForUser(Long user_id);
	
	@Query(value = "SELECT * FROM pet WHERE fk_device_id=?1", nativeQuery = true)
	Optional<Pet> getPetByDevice(Long device_id);
	
	@Query(value = "SELECT * FROM pet WHERE fk_zone_id=?1", nativeQuery = true)
	Collection<Pet> getAllPetsByZone(Long zone_id);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM pet WHERE fk_user_id = ?1", nativeQuery = true)
	void deleteByUser(Long user_id);
	
	
	void deleteById(Long pet_id);

	Optional<Pet> findByName(String name);
	
	Optional<Pet> findById(Long id);
}
