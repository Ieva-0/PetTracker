package com.pettracker.pettrackerserver.events;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

	@Query(value = "SELECT * FROM event WHERE fk_user_id = ?1", nativeQuery = true)
	Collection<Event> eventsForUser(Long user_id);

	@Query(value = "SELECT * FROM event WHERE fk_pet_id = ?1", nativeQuery = true)
	Collection<Event> eventsForPet(Long pet_id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM event WHERE fk_group_id = ?1", nativeQuery = true)
	void deleteForPetGroup(Long group_id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM event WHERE fk_device_id = ?1", nativeQuery = true)
	void deleteForDevice(Long device_id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM event WHERE fk_zone_id = ?1", nativeQuery = true)
	void deleteForZone(Long zone_id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM event WHERE fk_pet_id = ?1", nativeQuery = true)
	void deleteForPet(Long pet_id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM event WHERE fk_user_id = ?1", nativeQuery = true)
	void deleteForUser(Long user_id);

}
