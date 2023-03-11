package com.pettracker.pettrackerserver.model.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.pettracker.pettrackerserver.model.location_entry.LocationEntryRepository;

@Service
public class DeviceDao {
	@Autowired
	private DeviceRepository repository;
	@Autowired
	private LocationEntryRepository entryRepository;
	
	public Device save(Device device) {
		return repository.save(device);
	}
	
	public List<Device> getAllDevices() {
		List<Device> devices = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(devices::add);
		return devices;
	}
		
	public List<Device> getAllDevicesForUser(Long user_id) {
		List<Device> devices = new ArrayList<>();
		Streamable.of(repository.getAllDevicesForUser(user_id)).forEach(devices::add);
		return devices;
	}
	
	public Optional<Device> getDeviceById(Long device_id) {
		return repository.findById(device_id);
	}

	public void deleteDevice(Long device_id) {
		entryRepository.deleteForDevice(device_id);
		repository.deleteById(device_id);
		
	}

}
