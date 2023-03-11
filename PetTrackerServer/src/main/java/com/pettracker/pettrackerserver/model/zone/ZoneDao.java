package com.pettracker.pettrackerserver.model.zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.pettracker.pettrackerserver.model.zone_point.ZonePointRepository;

@Service
public class ZoneDao {
	@Autowired
	private ZoneRepository repository;
	
	@Autowired
	private ZonePointRepository zonePointRepository;
	
	public List<Zone> getAllZonesForUser(Long user_id) {
		List<Zone> zones = new ArrayList<>();
		Streamable.of(repository.getAllZonesForUser(user_id)).forEach(zones::add);
		return zones;
	}
	
	public Optional<Zone> getZoneById(Long zone_id) {
		return repository.findById(zone_id);
	}
	
	public Zone save(Zone zone) {
		return repository.save(zone);
	}

	public void deleteZone(Long zone_id) {
		repository.deleteById(zone_id);
		zonePointRepository.deleteByZone(zone_id);
	}
}
