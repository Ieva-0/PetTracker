package com.pettracker.pettrackerserver.model.device;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class DeviceDao {
	@Autowired
	private DeviceRepository repository;
	
	public Device save(Device device) {
		return repository.save(device);
	}
	
	public List<Device> getAllDevices() {
		List<Device> devices = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(devices::add);
		return devices;
	}
	
	public void delete(Device device) {
		repository.delete(device);
	}
}
