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
	
	public List<LocationEntry> locationHistoryForDevice(String device) {
		List<LocationEntry> locationEntries = new ArrayList<>();
		Streamable.of(repository.entryHistoryForDevice(device)).forEach(locationEntries::add);
		return locationEntries;
	}
	
	public LocationEntry lastEntryForDevice(String device) {
		return repository.lastEntryForDevice(device);
	}
	
	public List<LocationEntry> locationHistory() {
		List<LocationEntry> locationEntries = new ArrayList<>();
		Streamable.of(repository.entryHistory()).forEach(locationEntries::add);
		return locationEntries;
	}
	
	public LocationEntry lastEntry() {
		return repository.lastEntry();
	}


}
