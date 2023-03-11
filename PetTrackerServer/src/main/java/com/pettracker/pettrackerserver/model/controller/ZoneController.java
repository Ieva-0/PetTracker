package com.pettracker.pettrackerserver.model.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceDao;
import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntry;
import com.pettracker.pettrackerserver.model.zone.Zone;
import com.pettracker.pettrackerserver.model.zone.ZoneDao;
import com.pettracker.pettrackerserver.model.zone.ZoneRepository;
import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;
import com.pettracker.pettrackerserver.model.zone_point.ZonePointRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("zones")
public class ZoneController {
	
	@Autowired
	private ZoneDao zonedao;
	@Autowired
	private UserRepository userRepository;
	@Autowired 
	private ZoneRepository zoneRepository;
	@GetMapping("all")
	public List<Zone> getAllZones(@RequestParam Long user_id) {
		return zonedao.getAllZonesForUser(user_id);
	}
	
	@GetMapping("zone")
	public Zone getZoneById(@RequestParam Long zone_id) {
		Optional<Zone> result = zonedao.getZoneById(zone_id);
		if(result.isPresent()) {
			return result.get();
		}
		else return new Zone();	
	}
	
	@PostMapping("create")
	public Zone addZone(@RequestBody Zone zone) {
		return zonedao.save(zone);
	}
	@DeleteMapping("delete")
	public ResponseEntity<?> deleteDevice(@RequestHeader("Authorization") String token, @RequestParam Long zone_id) {
		String[] chunks = token.split("\\.");
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userRepository.findByUsername(username);
		Optional<Zone> zoneDetails = zoneRepository.findById(zone_id);
		if(userDetails.isPresent() && zoneDetails.isPresent()) 
		{
			Zone zone = zoneDetails.get();
			User user = userDetails.get();
			if(user.getId() == zone.getFk_user_id()) {
				zonedao.deleteZone(zone_id);
				return ResponseEntity.ok().body("all good");
			}else {
				return ResponseEntity.badRequest().body(new MessageResponse("Wrong user"));

			}
			
		}
//		System.out.println(userDetails.isPresent());
//		System.out.println(deviceDetails.isPresent());
		return ResponseEntity.badRequest().body(new MessageResponse("Error"));

	}
} 