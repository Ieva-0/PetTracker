package com.pettracker.pettrackerserver.pets;

import java.util.*;

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
import com.pettracker.pettrackerserver.pet_groups.PetGroup;
import com.pettracker.pettrackerserver.pet_groups.PetGroupDao;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroup;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroupDao;
import com.pettracker.pettrackerserver.pets.files.FileStorageService;
import com.pettracker.pettrackerserver.users.controllers.UserDao;
import com.pettracker.pettrackerserver.users.models.User;
import com.pettracker.pettrackerserver.users.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.zones.ZoneDao;
import com.pettracker.pettrackerserver.zones.Zone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;

@RestController
@RequestMapping("pets")
public class PetController {

	@Autowired
	private PetDao petDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private final FileStorageService fileStorageService;
	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private ZoneDao zoneDao;
	@Autowired
	private PetToGroupDao relationshipDao;
	@Autowired
	private PetGroupDao groupDao;
	@Autowired
	private EventDao eventDao;

	public PetController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@GetMapping("all")
	public List<PetWithPhoto> getAllPets(@RequestHeader("Authorization") String token, @RequestParam Long user_id) {
		List<Pet> allPets = petDao.getAllPetsForUser(user_id);
		List<PetWithPhoto> resultList = new ArrayList<PetWithPhoto>();
		for (Pet p : allPets) {
			PetWithPhoto temp = new PetWithPhoto();
			temp.setId(p.getId());
			temp.setName(p.getName());
			temp.setFk_device_id(p.getFk_device_id());
			temp.setFk_user_id(p.getFk_user_id());
			temp.setFk_zone_id(p.getFk_zone_id());
			temp.setNotifications(p.isNotifications());
			if (p.getPhoto() != null) {
				byte[] picture = fileStorageService.getFile(p.getPhoto());
				temp.setPicture(Base64.getEncoder().encodeToString(picture));

			}
			resultList.add(temp);
		}
		return resultList;
	}

	@GetMapping("all_nophoto")
	public List<Pet> getAllPetsWithoutPhotos(@RequestParam Long user_id) {
		return petDao.getAllPetsForUser(user_id);
	}

	@GetMapping("all_details")
	public List<PetWithDetails> getAllPetsWithDetails(@RequestHeader("Authorization") String token,
			@RequestParam Long user_id) {
		List<Pet> allPets = petDao.getAllPetsForUser(user_id);
		List<PetWithDetails> resultList = new ArrayList<PetWithDetails>();
		for (Pet p : allPets) {
			PetWithDetails temp = new PetWithDetails();
			temp.setId(p.getId());
			temp.setName(p.getName());
			if (p.getFk_device_id() != null)
				temp.setDevice(deviceDao.getDeviceById(p.getFk_device_id())
						.orElseThrow(() -> new RuntimeException("device not found with id: " + p.getFk_device_id())));
			if (p.getFk_zone_id() != null)
				temp.setZone(zoneDao.getZoneById(p.getFk_zone_id())
						.orElseThrow(() -> new RuntimeException("zone not found with id: " + p.getFk_zone_id())));
			temp.setFk_user_id(p.getFk_user_id());
			temp.setNotifications(p.isNotifications());
			List<PetToGroup> relationships = new ArrayList<>();
			Streamable.of(relationshipDao.getAllEntriesForPet(p.getId()))
					.forEach(relationships::add);
			for (PetToGroup r : relationships) {
				temp.getGroups().add(groupDao.getPetGroupById(r.getGroup_id())
						.orElseThrow(() -> new RuntimeException("group not found with id: " + r.getGroup_id())));
			}
			if (p.getPhoto() != null) {
				byte[] picture = fileStorageService.getFile(p.getPhoto());
				temp.setPicture(Base64.getEncoder().encodeToString(picture));

			}
			resultList.add(temp);
		}
		return resultList;
	}

	@GetMapping("pet_edit_create")
	public PetEditCreateResponse getPetWithDetails(@RequestHeader("Authorization") String token,
			@RequestParam Long pet_id) {
		PetEditCreateResponse resultObj = new PetEditCreateResponse();
		Optional<Pet> opt;
		if (!pet_id.equals(0L)) {
			opt = petDao.getPetById(pet_id);
			PetWithDetails result = new PetWithDetails();
			if (opt.isPresent()) {
				Pet p = opt.get();
				result.setId(p.getId());
				result.setName(p.getName());
				if (p.getFk_device_id() != null)
					result.setDevice(deviceDao.getDeviceById(p.getFk_device_id()).orElseThrow(
							() -> new RuntimeException("device not found with id: " + p.getFk_device_id())));
				if (p.getFk_zone_id() != null)
					result.setZone(zoneDao.getZoneById(p.getFk_zone_id())
							.orElseThrow(() -> new RuntimeException("zone not found with id: " + p.getFk_zone_id())));
				result.setFk_user_id(p.getFk_user_id());
				result.setNotifications(p.isNotifications());
				List<PetToGroup> relationships = new ArrayList<>();
				Streamable.of(relationshipDao.getAllEntriesForPet(p.getId()))
						.forEach(relationships::add);
				for (PetToGroup r : relationships) {
					result.getGroups().add(groupDao.getPetGroupById(r.getGroup_id())
							.orElseThrow(() -> new RuntimeException("group not found with id: " + r.getGroup_id())));
				}
				if (p.getPhoto() != null) {
					byte[] picture = fileStorageService.getFile(p.getPhoto());
					result.setPicture(Base64.getEncoder().encodeToString(picture));
				}
				resultObj.setPet(result);

			}
		}

		String[] chunks = token.split("\\.");

		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userDao.getByUsername(username);
		if (userDetails.isPresent()) {
			User u = userDetails.get();
			List<Zone> zones = new ArrayList<>();
			Streamable.of(zoneDao.getAllZonesForUser(u.getId())).forEach(zones::add);
			resultObj.setZones(zones);

			List<Device> devices = new ArrayList<>();
			Streamable.of(deviceDao.getAvailableDevicesForUser(u.getId())).forEach(devices::add);
			resultObj.setDevices(devices);
		}

		return resultObj;

	}

	@GetMapping("pet")
	public PetWithPhoto getPetWithPhotoById(@RequestHeader("Authorization") String token, @RequestParam Long pet_id) {
		Optional<Pet> result = petDao.getPetById(pet_id);
		if (result.isPresent()) {
			Pet p = result.get();
			PetWithPhoto temp = new PetWithPhoto();
			temp.setId(p.getId());
			temp.setName(p.getName());
			temp.setFk_device_id(p.getFk_device_id());
			temp.setFk_user_id(p.getFk_user_id());
			temp.setFk_zone_id(p.getFk_zone_id());
			temp.setNotifications(p.isNotifications());
			if (p.getPhoto() != null) {
				byte[] picture = fileStorageService.getFile(p.getPhoto());
				temp.setPicture(Base64.getEncoder().encodeToString(picture));

			}
			return temp;
		} else
			return new PetWithPhoto();
	}

	@PostMapping("save")
	public MessageResponse savePet(@RequestHeader("Authorization") String token, @RequestBody PetWithPhoto petwithphoto) {
		User user = getUserByToken(token);
		List<Pet> pets = petDao.getPetByName(petwithphoto.getName());
		if (pets.size() > 0) {
			for(Pet p : pets) {
				if (p.getFk_user_id().equals(user.getId()) && (petwithphoto.getId() == null || !p.getId().equals(petwithphoto.getId()))) {
					return new MessageResponse(false, "Pet name is already used.");
				}
			}
			
		}
		
		Pet pet = new Pet();
		if (petwithphoto.getId() != null)
			pet.setId(petwithphoto.getId());
		pet.setName(petwithphoto.getName());
		pet.setFk_device_id(petwithphoto.getFk_device_id());
		pet.setFk_user_id(petwithphoto.getFk_user_id());
		pet.setFk_zone_id(petwithphoto.getFk_zone_id());
		pet.setNotifications(petwithphoto.isNotifications());
		if (petwithphoto.getPicture() != null) {
			String fileName = fileStorageService.storeFile(Base64.getDecoder().decode(petwithphoto.getPicture()));
			pet.setPhoto(fileName);

		}
		petDao.save(pet);
		return new MessageResponse(true, "Pet saved successfully!");
	}

	@DeleteMapping("delete")
	public MessageResponse deletePet(@RequestHeader("Authorization") String token, @RequestParam Long pet_id) {
		Optional<Pet> petDetails = petDao.getPetById(pet_id);
		if (petDetails.isPresent()) {
			User user = getUserByToken(token);
			Pet pet = petDetails.get();
			if (user.getId() == pet.getFk_user_id()) {
				eventDao.deleteForPet(pet_id);
				relationshipDao.deleteAllForPet(pet_id);
				petDao.deletePet(pet_id);
				return new MessageResponse(true, "Pet deleted successfully.");
			} else {
				return new MessageResponse(false, "Bad token.");

			}

		}
		return new MessageResponse(false, "Couldn't find pet.");

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