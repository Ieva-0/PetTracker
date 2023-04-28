package com.pettracker.pettrackerserver.model.pet_group;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface PetToGroupRepository extends CrudRepository<PetToGroup, Long>, JpaRepository<PetToGroup, Long> {
	@Query(value = "SELECT * FROM pet_to_group WHERE pet_id=?1", nativeQuery = true)
	Collection<PetToGroup> getAllPetToGroupRelationshipsForPet(Long pet_id);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM pet_to_group WHERE pet_id=?1", nativeQuery = true)
	void deletePetToGroupRelationshipsForPet(Long pet_id);
	
	@Query(value = "SELECT * FROM pet_to_group WHERE group_id=?1", nativeQuery = true)
	Collection<PetToGroup> getAllPetToGroupRelationshipsForGroup(Long group_id);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM pet_to_group WHERE group_id=?1", nativeQuery = true)
	void deletePetToGroupRelationshipsForGroup(Long group_id);
	
	void deleteById(Long pet_group_id);
	
	Optional<PetToGroup> findById(Long id);
	
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM pet_to_group WHERE group_id = ?1", nativeQuery = true)
	void deleteByGroup(Long group_id);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM pet_to_group WHERE pet_id = ?1", nativeQuery = true)
	void deleteByPet(Long pet_id);
}
