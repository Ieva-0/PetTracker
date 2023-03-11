package com.pettracker.pettrackerserver.model.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceDao;
import com.pettracker.pettrackerserver.model.device.DeviceRepository;
import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;

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
@RequestMapping("devices")
public class DeviceController {
	
	@Autowired
	private DeviceDao devicedao;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	
	@GetMapping("all")
	public List<Device> getAllDevices(@RequestParam Long user_id) {
		return devicedao.getAllDevicesForUser(user_id);
	}
	
	@GetMapping("device")
	public Device getDeviceById(@RequestParam Long device_id) {
		Optional<Device> result = devicedao.getDeviceById(device_id);
		if(result.isPresent()) {
			return result.get();
		}
		else return new Device();
	}
	
	@PostMapping("create")
	public Device addDevice(@RequestBody Device device) {
		return devicedao.save(device);
	}
	
	@DeleteMapping("delete")
	public ResponseEntity<?> deleteDevice(@RequestHeader("Authorization") String token, @RequestParam Long device_id) {
		String[] chunks = token.split("\\.");
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userRepository.findByUsername(username);
		Optional<Device> deviceDetails = deviceRepository.findById(device_id);
		if(userDetails.isPresent() && deviceDetails.isPresent()) 
		{
			Device device = deviceDetails.get();
			User user = userDetails.get();
			if(user.getId() == device.getFk_user_id()) {
				devicedao.deleteDevice(device_id);
				return ResponseEntity.ok().body("all good");
			}else {
				return ResponseEntity.badRequest().body(new MessageResponse("Wrong user"));

			}
			
		}
//		System.out.println(userDetails.isPresent());
//		System.out.println(deviceDetails.isPresent());
		return ResponseEntity.badRequest().body(new MessageResponse("Error"));

	}
	
	@PostMapping("edit")
	public Device editDevice(@RequestBody Device device) {
		return devicedao.save(device);
	}
	
} 