package com.pettracker.pettrackerserver.model.location_entry;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationEntryRepository extends CrudRepository<LocationEntry, Integer> {
	
	@Query(value = "SELECT * FROM location_entry WHERE device_id_foreign = ?1 ORDER BY id DESC LIMIT 100", nativeQuery = true)
	Collection<LocationEntry> entryHistoryForDevice(String device);

	@Query(value = "SELECT * FROM location_entry WHERE device_id_foreign = ?1 ORDER BY ID DESC LIMIT 1", nativeQuery = true)
	LocationEntry lastEntryForDevice(String device);
	
	@Query(value = "SELECT * FROM location_entry ORDER BY id DESC LIMIT 100", nativeQuery = true)
	Collection<LocationEntry> entryHistory();

	@Query(value = "SELECT * FROM location_entry ORDER BY ID DESC LIMIT 1", nativeQuery = true)
	LocationEntry lastEntry();
}
