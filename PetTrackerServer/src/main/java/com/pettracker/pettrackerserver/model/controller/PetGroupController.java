package com.pettracker.pettrackerserver.model.controller;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceDao;
import com.pettracker.pettrackerserver.model.device.DeviceRepository;
import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;
import com.pettracker.pettrackerserver.model.pet.FileStorageService;
import com.pettracker.pettrackerserver.model.pet.Pet;
import com.pettracker.pettrackerserver.model.pet.PetDao;
import com.pettracker.pettrackerserver.model.pet.PetRepository;
import com.pettracker.pettrackerserver.model.pet_group.PetGroup;
import com.pettracker.pettrackerserver.model.pet_group.PetGroupCreateRequest;
import com.pettracker.pettrackerserver.model.pet_group.PetGroupDao;
import com.pettracker.pettrackerserver.model.pet_group.PetGroupRepository;
import com.pettracker.pettrackerserver.model.pet_group.PetToGroup;
import com.pettracker.pettrackerserver.model.pet_group.PetToGroupDao;
import com.pettracker.pettrackerserver.model.zone.Zone;
import com.pettracker.pettrackerserver.model.zone.ZoneCreateRequest;
import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.util.*;

@RestController
@RequestMapping("pet_groups")
public class PetGroupController {
	
	@Autowired
	private PetGroupDao petgroupdao;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PetGroupRepository petGroupRepository;
	@Autowired
	private PetToGroupDao relationshipdao;
	
	@Autowired
	private PetDao petdao;
	public PetGroupController() {
	}
	
	@GetMapping("all")
	public List<PetGroup> getAllPetGroups(@RequestParam Long user_id) {
		return petgroupdao.getAllPetGroupsForUser(user_id);
	}
	
	@GetMapping("group") 
	public PetGroup getPetGroupById(@RequestParam Long pet_group_id) {
		Optional<PetGroup> opt = petgroupdao.getPetGroupById(pet_group_id);
		if(opt.isPresent())
			return opt.get();
		else return null;
	}
	
	@GetMapping("pet")
	public List<PetGroup> getGroupsForPet(@RequestParam Long pet_id) {
		List<PetToGroup> relationships = relationshipdao.getAllEntriesForPet(pet_id);
		List<PetGroup> result = new ArrayList<>();
		for(PetToGroup r : relationships) {
			Optional<PetGroup> gr = petgroupdao.getPetGroupById(r.getGroup_id());
			if(gr.isPresent()) {
				result.add(gr.get());
			}
		}
		return result;
	}
	
	@GetMapping("pets")
	public List<Pet> getPetsForGroup(@RequestParam Long group_id) {
		List<PetToGroup> relationships = relationshipdao.getAllEntriesForGroup(group_id);
		List<Pet> result = new ArrayList<>();
		for(PetToGroup r : relationships) {
			Optional<Pet> p = petdao.getPetById(r.getPet_id());
			if(p.isPresent()) {
				result.add(p.get());
			}
		}
		return result;
	}
	
	@PostMapping("create")
	public PetGroup addPetGroup(@RequestHeader("Authorization") String token, @RequestBody PetGroupCreateRequest groupreq) {
		PetGroup group = new PetGroup();
		User u = getUserByToken(token);
		if(u != null) {
			if(groupreq.getGroup_id() != null) {
				group.setId(groupreq.getGroup_id());
				relationshipdao.deleteAllForGroup(groupreq.getGroup_id());
			}
			group.setFk_user_id(u.getId());
			group.setName(groupreq.getGroup_name());
			if(groupreq.getZone_id() != null) {
				group.setFk_zone_id(groupreq.getZone_id());
			}
			group.setNotifications(groupreq.isNotifications());
			
			group = petgroupdao.save(group);
			List<PetToGroup> relationships = new ArrayList<PetToGroup>();
			List<Pet> pets = groupreq.getPets();
			for(Pet p : pets) {
				PetToGroup temp = new PetToGroup();
				temp.setGroup_id(group.getId());
				temp.setPet_id(p.getId());
				relationships.add(temp);
			}
			relationshipdao.saveAllPetToGroupRelationships(relationships);
		}
		return group;
	}
	@DeleteMapping("delete")
	public ResponseEntity<?> deletePetGroup(@RequestHeader("Authorization") String token, @RequestParam Long pet_group_id) {
		String[] chunks = token.split("\\.");
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userRepository.findByUsername(username);
		Optional<PetGroup> petGroupDetails = petGroupRepository.findById(pet_group_id);
		if(userDetails.isPresent() && petGroupDetails.isPresent()) 
		{
			PetGroup pet_group = petGroupDetails.get();
			User user = userDetails.get();
			if(user.getId().equals(pet_group.getFk_user_id())) {
				petgroupdao.deletePetGroup(pet_group_id);
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
	public PetGroup editPetGroup(@RequestBody PetGroup pet_group) {
		return petgroupdao.save(pet_group);
	}
	
	public User getUserByToken(String token) {
		String[] chunks = token.split("\\.");
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userRepository.findByUsername(username);
		if(userDetails.isPresent()) {
			return userDetails.get();
		} else {
			return null;
		}
	}
	
	@GetMapping("amount")
	public Integer petAmountInGroup(@RequestHeader("Authorization") String token, @RequestParam Long pet_group_id) {
		return relationshipdao.getAllEntriesForGroup(pet_group_id).size();
	}
	
} 