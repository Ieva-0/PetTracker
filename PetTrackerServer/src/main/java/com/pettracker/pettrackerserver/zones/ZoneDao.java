package com.pettracker.pettrackerserver.zones;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class ZoneDao {
	@Autowired
	private ZoneRepository repository;
	
	public List<Zone> getAllZonesForUser(Long user_id) {
		List<Zone> zones = new ArrayList<>();
		Streamable.of(repository.getAllZonesForUser(user_id)).forEach(zones::add);
		return zones;
	}
	
	public Optional<Zone> getZoneById(Long zone_id) {
		return repository.findById(zone_id);
	}
	
	public Optional<Zone> getZoneByName(String name) {
		return repository.findByName(name);
	}
	
	public Zone save(Zone zone) {
		return repository.save(zone);
	}

	public void deleteZone(Long zone_id) {
		repository.deleteById(zone_id);
	}
	
	public void deleteZonesByUser(Long user_id) {
		repository.deleteByUser(user_id);
	}
}
