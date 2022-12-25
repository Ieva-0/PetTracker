package com.pettracker.pettrackerserver.model.controller;

import java.util.List;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {
	
	@Autowired
	private DeviceDao devicedao;
	
	@GetMapping("/device/all")
	public List<Device> getAllDevices() {
		return devicedao.getAllDevices();
	}
	
	@PostMapping("/device/create")
	public Device addDevice(@RequestBody Device device) {
		return devicedao.save(device);
	}
}