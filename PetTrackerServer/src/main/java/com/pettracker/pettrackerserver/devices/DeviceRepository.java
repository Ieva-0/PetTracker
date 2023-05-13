package com.pettracker.pettrackerserver.devices;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Long>, JpaRepository<Device, Long> {
	@Query(value = "SELECT * FROM device WHERE fk_user_id=?1", nativeQuery = true)
	Collection<Device> getAllDevicesForUser(Long user_id);

	@Query(value = "SELECT device.* FROM device LEFT JOIN pet ON pet.fk_device_id=device.id WHERE pet.name is null AND device.fk_user_id=?1", nativeQuery = true)
	Collection<Device> getAvailableDevicesForUser(Long user_id);

	void deleteById(Long device_id);

	Optional<Device> findByName(String name);

	Optional<Device> findById(Long id);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM device WHERE fk_user_id = ?1", nativeQuery = true)
	void deleteByUser(Long user_id);
}
