package com.pettracker.pettrackerserver.model.zone;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneRepository extends CrudRepository<Zone, Long> {
	@Query(value = "SELECT * FROM zone WHERE fk_user_id=?1", nativeQuery = true)
	Collection<Zone> getAllZonesForUser(Long user_id);
	
	void deleteById(Long zone_id);
	
	Optional<Zone> findById(Long id);
}
