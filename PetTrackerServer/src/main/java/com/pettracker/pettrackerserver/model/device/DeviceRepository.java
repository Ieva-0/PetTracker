package com.pettracker.pettrackerserver.model.device;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntry;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Long>, JpaRepository<Device, Long> {
	@Query(value = "SELECT * FROM device WHERE fk_user_id=?1", nativeQuery = true)
	Collection<Device> getAllDevicesForUser(Long user_id);
	
	void deleteById(Long device_id);

	Optional<Device> findByName(String name);
	
	Optional<Device> findById(Long id);
}
