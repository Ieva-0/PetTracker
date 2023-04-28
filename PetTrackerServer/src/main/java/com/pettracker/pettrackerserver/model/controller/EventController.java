package com.pettracker.pettrackerserver.model.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceRepository;
import com.pettracker.pettrackerserver.model.event.Event;
import com.pettracker.pettrackerserver.model.event.EventDao;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("events")
public class EventController {
	
	@Autowired
	private EventDao eventdao;

	
	@GetMapping("all")
	public List<Event> allEvents(@RequestHeader("Authorization") String token) {
		return eventdao.allEvents();
	}
	
	@GetMapping("pet")
	public List<Event> eventsForPet(@RequestHeader("Authorization") String token, @RequestParam Long pet_id) {
		return eventdao.eventsForPet(pet_id);
	}
	

}