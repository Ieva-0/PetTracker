package com.pettracker.pettrackerserver.zones;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ZoneRepository extends CrudRepository<Zone, Long> {
	@Query(value = "SELECT * FROM zone WHERE fk_user_id=?1", nativeQuery = true)
	Collection<Zone> getAllZonesForUser(Long user_id);
	
	void deleteById(Long zone_id);
	
	Optional<Zone> findById(Long id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM zone WHERE fk_user_id = ?1", nativeQuery = true)
	void deleteByUser(Long user_id);

	Optional<Zone> findByName(String name);
	
}
