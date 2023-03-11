package com.pettracker.pettrackerserver.model.location_entry;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class LocationEntryDao {
	@Autowired
	private LocationEntryRepository repository;
	
	public List<LocationEntry> locationHistoryForDevice(Long device) {
		List<LocationEntry> locationEntries = new ArrayList<>();
		Streamable.of(repository.entryHistoryForDevice(device)).forEach(locationEntries::add);
		return locationEntries;
	}
	
	public LocationEntry lastEntryForDevice(Long device) {
		return repository.lastEntryForDevice(device);
	}
	
	public LocationEntry newEntryForDevice(LocationEntry entry) {
		return repository.save(entry);
	}


}
