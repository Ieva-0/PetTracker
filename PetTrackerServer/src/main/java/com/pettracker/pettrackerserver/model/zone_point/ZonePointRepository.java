package com.pettracker.pettrackerserver.model.zone_point;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ZonePointRepository extends CrudRepository<ZonePoint, Long> {
	
	@Query(value = "SELECT * FROM zone_point WHERE fk_zone_id = ?1 ORDER BY list_index ASC", nativeQuery = true)
	Collection<ZonePoint> getAllZonePoints(Long zone_id);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM location_entry WHERE fk_zone_id = ?1", nativeQuery = true)
	void deleteByZone(Long zone_id);
}
