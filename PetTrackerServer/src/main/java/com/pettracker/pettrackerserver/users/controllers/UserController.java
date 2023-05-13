package com.pettracker.pettrackerserver.users.controllers;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettracker.pettrackerserver.devices.Device;
import com.pettracker.pettrackerserver.devices.DeviceDao;
import com.pettracker.pettrackerserver.events.EventDao;
import com.pettracker.pettrackerserver.location_entries.LocationEntryDao;
import com.pettracker.pettrackerserver.pet_groups.PetGroup;
import com.pettracker.pettrackerserver.pet_groups.PetGroupDao;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroupDao;
import com.pettracker.pettrackerserver.pets.PetDao;
import com.pettracker.pettrackerserver.users.models.ChangePasswordRequest;
import com.pettracker.pettrackerserver.users.models.User;
import com.pettracker.pettrackerserver.users.models.UsersWithRolesResponse;
import com.pettracker.pettrackerserver.users.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.users.repository.RefreshTokenRepository;
import com.pettracker.pettrackerserver.zones.ZoneDao;
import com.pettracker.pettrackerserver.zones.Zone;
import com.pettracker.pettrackerserver.zones.zone_point.ZonePointDao;

@RestController 
@RequestMapping("users")
public class UserController {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PetGroupDao groupDao;
	
	@Autowired 
	private PetDao petDao;
	
	@Autowired
	private ZoneDao zoneDao;
	
	@Autowired 
	private DeviceDao deviceDao;
	
	@Autowired
	private EventDao eventDao;
	
	@Autowired
	private PetToGroupDao relationshipDao;
	
	@Autowired
	private ZonePointDao zonePointDao;
	
	@Autowired
	private RefreshTokenRepository tokenRepository;
	
	@Autowired
	private LocationEntryDao locationEntryDao;
	
	@Autowired
	private RoleDao roledao;
	
	@GetMapping("all")
	public List<User> allUsers(@RequestHeader("Authorization") String token) {
		return userDao.allUsers();
	}
	
	@GetMapping("user")
	public User getUser(@RequestHeader("Authorization") String token) {
		return getUserByToken(token);
	}
	
	@GetMapping("all_with_roles")
	public UsersWithRolesResponse allUsersWithRoles(@RequestHeader("Authorization") String token) {
		UsersWithRolesResponse resultObj = new UsersWithRolesResponse();
		resultObj.setUsers(userDao.allUsers());
		resultObj.setRoles(roledao.allRoles());
		return resultObj;
	}
	
	@PostMapping("toggle_block")
	public MessageResponse toggleBlockUser(@RequestParam("user_id") Long user_id) {
		Optional<User> u = userDao.getById(user_id);
		if(u.isPresent()) {
			User user = u.get();
			user.setBlocked(!user.isBlocked());
			userDao.save(user);
			if(user.isBlocked())
				return new MessageResponse(true, "Successfully blocked user.");
			else return new MessageResponse(true, "Successfully unblocked user.");
		}
		return new MessageResponse(false, "Something went wrong.");

	}
	
	@DeleteMapping("/user") 
	public MessageResponse deleteUser(@RequestHeader("Authorization") String token, @RequestParam Long user_id) {
		eventDao.deleteForUser(user_id);
		tokenRepository.deleteByUser(user_id);
		List<PetGroup> pet_groups = new ArrayList<>();
		Streamable.of(groupDao.getAllPetGroupsForUser(user_id)).forEach(pet_groups::add);
		for(PetGroup g : pet_groups) {
			relationshipDao.deleteAllForGroup(g.getId());
		}
		groupDao.deletePetGroupsForUser(user_id);
		petDao.deletePetsForUser(user_id);
		List<Zone> zones = new ArrayList<>();
		Streamable.of(zoneDao.getAllZonesForUser(user_id)).forEach(zones::add);
		for(Zone z : zones) {
			zonePointDao.deleteZonePoints(z.getId());
		}
		zoneDao.deleteZonesByUser(user_id);
		List<Device> devices = new ArrayList<>();
		Streamable.of(deviceDao.getAllDevicesForUser(user_id)).forEach(devices::add);
		for(Device d : devices) {
			locationEntryDao.deleteForDevice(d.getId());
		}
		deviceDao.deleteDevicesByUser(user_id);
		userDao.deleteById(user_id);
		return new MessageResponse(true, "User deleted successfully!");

	}
	@PostMapping("/change_password")
	public MessageResponse changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequest req) {
		User user = getUserByToken(token);
		if (!user.getPassword().toUpperCase().trim().equals(req.getOld_pw().toUpperCase().trim())) {
			return new MessageResponse(false, "Incorrect current password entered.");
		} else {
			user.setPassword(req.getNew_pw());
			userDao.save(user);
			return new MessageResponse(true, "Password successfully changed!");

		}
	}
	
	@PostMapping("/edit_profile")
	public MessageResponse editProfile(@RequestHeader("Authorization") String token, @RequestBody User user_edited) {
		User user = getUserByToken(token);
		Optional<User> userDetails = userDao.getByUsername(user_edited.getUsername());
		if (userDetails.isPresent()) {
			User temp = userDetails.get();
			if (!temp.getId().equals(user.getId())) {
				return new MessageResponse(false, "Username not available.");
			}
		}
		userDetails = userDao.getByEmail(user_edited.getEmail());
		if (userDetails.isPresent()) {
			User temp = userDetails.get();
			if (!temp.getId().equals(user.getId())) {
				return new MessageResponse(false, "Email not available.");
			}
		}
		if (!user.getPassword().toUpperCase().trim().equals(user_edited.getPassword().toUpperCase().trim())) {
			return new MessageResponse(false, "Incorrect password entered.");
		} 
		userDao.save(user_edited);
		return new MessageResponse(true, "Profile changes successfully saved!");	
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