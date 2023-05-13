package com.pettracker.pettrackerserver.pet_groups;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PetGroupRepository extends CrudRepository<PetGroup, Long>, JpaRepository<PetGroup, Long> {
	@Query(value = "SELECT * FROM pet_group WHERE fk_user_id=?1", nativeQuery = true)
	Collection<PetGroup> getAllPetGroupsForUser(Long user_id);

	@Query(value = "SELECT * FROM pet_group WHERE fk_zone_id=?1", nativeQuery = true)
	Collection<PetGroup> getAllPetGroupsByZone(Long zone_id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM pet_group WHERE fk_user_id = ?1", nativeQuery = true)
	void deleteByUser(Long user_id);

	void deleteById(Long pet_group_id);

	Optional<PetGroup> findByName(String name);

	Optional<PetGroup> findById(Long id);
}
