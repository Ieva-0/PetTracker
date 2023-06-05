package com.pettracker.pettrackerserver.devices;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettracker.pettrackerserver.events.EventDao;
import com.pettracker.pettrackerserver.location_entries.LocationEntryDao;
import com.pettracker.pettrackerserver.pets.Pet;
import com.pettracker.pettrackerserver.pets.PetDao;
import com.pettracker.pettrackerserver.users.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.users.controllers.UserDao;
import com.pettracker.pettrackerserver.users.models.User;

@RestController
@RequestMapping("devices")
public class DeviceController {

	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private PetDao petDao;
	@Autowired
	private EventDao eventDao;
	@Autowired
	private LocationEntryDao locationEntryDao;

	@GetMapping("all")
	public List<Device> getAllDevices(@RequestHeader("Authorization") String token, @RequestParam Long user_id) {
		return deviceDao.getAllDevicesForUser(user_id);
	}

	@GetMapping("all_details")
	public List<DeviceWithDetails> getAllDevicesWithDetails(@RequestHeader("Authorization") String token,
			@RequestParam Long user_id) {
		List<Device> all = deviceDao.getAllDevicesForUser(user_id);
		List<DeviceWithDetails> result = new ArrayList<DeviceWithDetails>();
		for (Device d : all) {
			DeviceWithDetails temp = new DeviceWithDetails();
			temp.setId(d.getId());
			temp.setName(d.getName());
			temp.setPassword(d.getPassword());
			temp.setFk_user_id(d.getFk_user_id());
			Optional<Pet> pet = petDao.getPetByDevice(d.getId());
			if (pet.isPresent()) {
				temp.setPet(pet.get());
			}
			result.add(temp);
		}
		return result;
	}

	@GetMapping("available")
	public List<Device> getAvailableDevices(@RequestHeader("Authorization") String token, @RequestParam Long user_id) {
		return deviceDao.getAvailableDevicesForUser(user_id);
	}

	@GetMapping("device")
	public Device getDeviceById(@RequestHeader("Authorization") String token, @RequestParam Long device_id) {
		Optional<Device> result = deviceDao.getDeviceById(device_id);
		if (result.isPresent()) {
			return result.get();
		} else
			return new Device();
	}

	@PostMapping("save")
	public MessageResponse addDevice(@RequestHeader("Authorization") String token, @RequestBody Device device) {
		User user = getUserByToken(token);
		List<Device> devices = deviceDao.getDevicesByName(device.getName());
		if (devices.size() > 0) {
			for(Device d : devices) {
				if (d.getFk_user_id().equals(user.getId()) && (device.getId() == null || !d.getId().equals(device.getId()))) {
					return new MessageResponse(false, "Device name is already used.");
				}
			}
			
		}
		deviceDao.save(device);
		return new MessageResponse(true, "Device successfully saved!");
	}

	@DeleteMapping("delete")
	public MessageResponse deleteDevice(@RequestHeader("Authorization") String token, @RequestParam Long device_id) {
		Optional<Device> deviceDetails = deviceDao.getDeviceById(device_id);
		if (deviceDetails.isPresent()) {
			User user = getUserByToken(token);
			Device device = deviceDetails.get();
			if (user.getId() == device.getFk_user_id()) {
				eventDao.deleteForDevice(device_id);
				deviceDao.deleteDevice(device_id);
				locationEntryDao.deleteForDevice(device_id);
				return new MessageResponse(true, "Device deleted successfully.");
			} else {
				return new MessageResponse(false, "Bad token.");

			}

		}
		return new MessageResponse(false, "Couldn't find device.");

	}

	public User getUserByToken(String token) {
		String[] chunks = token.split("\\.");

		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userDao.getByUsername(username);
		if (userDetails.isPresent()) {
			return userDetails.get();
		} else {
			return null;
		}
	}
}