package com.pettracker.pettrackerserver.location_entries;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LocationEntryRepository extends CrudRepository<LocationEntry, Long> {

	@Query(value = "SELECT * FROM location_entry WHERE fk_device_id = ?1 ORDER BY id DESC LIMIT 100", nativeQuery = true)
	Collection<LocationEntry> entryHistoryForDevice(Long device);

	@Query(value = "SELECT * FROM location_entry WHERE fk_device_id = ?1 ORDER BY ID DESC LIMIT 1", nativeQuery = true)
	LocationEntry lastEntryForDevice(Long device);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM location_entry WHERE fk_device_id = ?1", nativeQuery = true)
	void deleteForDevice(Long device_id);
}
