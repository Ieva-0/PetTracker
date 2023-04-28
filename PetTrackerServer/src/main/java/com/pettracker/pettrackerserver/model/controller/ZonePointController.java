package com.pettracker.pettrackerserver.model.controller;


import java.util.Base64;
import java.util.List;
import java.util.Optional;

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

import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;
import com.pettracker.pettrackerserver.model.zone.Zone;
import com.pettracker.pettrackerserver.model.zone.ZoneDao;
import com.pettracker.pettrackerserver.model.zone.ZoneRepository;
import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;
import com.pettracker.pettrackerserver.model.zone_point.ZonePointDao;
@RestController
@RequestMapping("zone_points")
public class ZonePointController {
	
	@Autowired
	private ZonePointDao zonepointdao;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ZoneRepository zoneRepository;
	@GetMapping("zone")
	public List<ZonePoint> getAllZonePoints(@RequestHeader("Authorization") String token, @RequestParam Long zone_id) {
		return zonepointdao.getAllZonePoints(zone_id);
	}
	
	@PostMapping("create")
	public List<ZonePoint> addZonePoints(@RequestHeader("Authorization") String token, @RequestBody List<ZonePoint> points) {
		if(points.size() != 0)
			zonepointdao.deleteZonePoints(points.get(0).getFk_zone_id());
		return zonepointdao.saveAllZonePoints(points);
	}
	@DeleteMapping("delete")
	public ResponseEntity<?> deleteZonePoints(@RequestHeader("Authorization") String token, @RequestParam Long fk_zone_id) {
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userRepository.findByUsername(username);
		Optional<Zone> zoneDetails = zoneRepository.findById(fk_zone_id);
		if(userDetails.isPresent() && zoneDetails.isPresent()) 
		{
			Zone zone = zoneDetails.get();
			User user = userDetails.get();
			if(user.getId() == zone.getFk_user_id()) {
				zonepointdao.deleteZonePoints(fk_zone_id);
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
