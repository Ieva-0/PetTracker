package com.pettracker.pettrackerserver.model.controller;

import java.util.List;

import com.pettracker.pettrackerserver.model.location_entry.LocationEntry;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationEntryController {
	
	@Autowired
	private LocationEntryDao locationdao;

	@GetMapping("/location/history")
	public List<LocationEntry> locationHistoryForDevice(@RequestParam String device_id_foreign) {
		return locationdao.locationHistoryForDevice(device_id_foreign);
	}
	
	@GetMapping("/location/last")
	public LocationEntry lastEntryForDevice(@RequestParam String device_id_foreign) {
		return locationdao.lastEntryForDevice(device_id_foreign);
	}
//	
//	@GetMapping("/location/history")
//	public List<LocationEntry> locationHistory() {
//		return locationdao.locationHistory();
//	}
//	
//	@GetMapping("/location/last")
//	public LocationEntry lastEntry() {
//		return locationdao.lastEntry();
//	}
}