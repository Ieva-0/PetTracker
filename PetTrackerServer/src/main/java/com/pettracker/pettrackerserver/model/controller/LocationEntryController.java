package com.pettracker.pettrackerserver.model.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceRepository;
import com.pettracker.pettrackerserver.model.jwt.models.RefreshToken;
import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.response.JwtResponse;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntry;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntryDao;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntryRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("locations")
public class LocationEntryController {
	
	@Autowired
	private LocationEntryDao locationdao;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@GetMapping("history")
	public List<LocationEntry> locationHistoryForDevice(@RequestParam Long device_id) {
		return locationdao.locationHistoryForDevice(device_id);
	}
	
	@GetMapping("last")
	public LocationEntry lastEntryForDevice(@RequestParam Long device_id) {
		return locationdao.lastEntryForDevice(device_id);
	}
	
	@PostMapping("new")
	public ResponseEntity<?> addLocationEntryForDevice(@RequestBody LocationEntryRequest req) {
		if (!userRepository.existsByUsername(req.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username doesn't exist!"));

		} else {
			Optional<User> userDetails = userRepository.findByUsername(req.getUsername());
			Optional<Device> deviceDetails = deviceRepository.findByName(req.getDevice_name());

			if(userDetails.isPresent() && deviceDetails.isPresent()) 
			{	
				User user = userDetails.get();
				Device device = deviceDetails.get();
				
				if(!(device.getFk_user_id() == user.getId()))
					return ResponseEntity.badRequest().body(new MessageResponse("Error: device user id and user id do not match!"));
				
				if(!(device.getPassword().toUpperCase().trim().equals(req.getDevice_password().toUpperCase().trim()))) 
					return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is inccorrect!"));
				
				LocationEntry entry = new LocationEntry();
				entry.setLatitude(req.getLattitude());
				entry.setLongitude(req.getLongitude());
				entry.setFk_device_id(device.getId());
				entry.setCreated_at(System.currentTimeMillis());
				entry.setUsed_at(System.currentTimeMillis());
				locationdao.newEntryForDevice(entry);
				return ResponseEntity.ok("Entry saved");
				
			}
			
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Error: entry save failed"));
				
	}

}