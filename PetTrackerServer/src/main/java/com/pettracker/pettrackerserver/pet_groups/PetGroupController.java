package com.pettracker.pettrackerserver.pet_groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettracker.pettrackerserver.devices.Device;
import com.pettracker.pettrackerserver.events.EventDao;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroup;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroupDao;
import com.pettracker.pettrackerserver.pets.Pet;
import com.pettracker.pettrackerserver.pets.PetDao;
import com.pettracker.pettrackerserver.users.controllers.UserDao;
import com.pettracker.pettrackerserver.users.models.User;
import com.pettracker.pettrackerserver.users.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.zones.ZoneDao;
import com.pettracker.pettrackerserver.zones.Zone;

import java.util.*;

@RestController
@RequestMapping("pet_groups")
public class PetGroupController {

	@Autowired
	private PetGroupDao petGroupDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private PetToGroupDao relationshipDao;
	@Autowired
	private PetDao petDao;
	@Autowired
	private ZoneDao zoneDao;
	@Autowired
	private EventDao eventDao;

	public PetGroupController() {
	}

	@GetMapping("all")
	public List<PetGroup> getAllPetGroups(@RequestParam Long user_id) {
		return petGroupDao.getAllPetGroupsForUser(user_id);
	}

	@GetMapping("all_details")
	public List<PetGroupWithDetails> getAllPetGroupsWithDetails(@RequestParam Long user_id) {
		List<PetGroup> all = petGroupDao.getAllPetGroupsForUser(user_id);
		List<PetGroupWithDetails> result = new ArrayList<PetGroupWithDetails>();
		for (PetGroup g : all) {
			PetGroupWithDetails temp = new PetGroupWithDetails();
			temp.setId(g.getId());
			temp.setName(g.getName());
			temp.setFk_user_id(g.getFk_user_id());
			temp.setNotifications(g.isNotifications());
			List<PetToGroup> relationships = new ArrayList<>();
			Streamable.of(relationshipDao.getAllEntriesForGroup(g.getId())).forEach(relationships::add);
			for (PetToGroup r : relationships) {
				temp.getPets().add(petDao.getPetById(r.getPet_id())
						.orElseThrow(() -> new RuntimeException("pet not found with id: " + r.getGroup_id())));
			}
			if (g.getFk_zone_id() != null)
				temp.setZone(zoneDao.getZoneById(g.getFk_zone_id())
						.orElseThrow(() -> new RuntimeException("zone not found with id: " + g.getFk_zone_id())));

			result.add(temp);
		}
		return result;
	}

	@GetMapping("group_create_edit")
	public PetGroupCreateEditResponse getPetGroupById(@RequestHeader("Authorization") String token,
			@RequestParam Long pet_group_id) {
		Optional<PetGroup> opt = petGroupDao.getPetGroupById(pet_group_id);
		PetGroupCreateEditResponse resultObj = new PetGroupCreateEditResponse();
		PetGroupWithDetails result = new PetGroupWithDetails();
		if (opt.isPresent()) {
			PetGroup p = opt.get();
			result.setId(p.getId());
			result.setFk_user_id(p.getFk_user_id());
			result.setName(p.getName());
			if (p.getFk_zone_id() != null)
				result.setZone(zoneDao.getZoneById(p.getFk_zone_id())
						.orElseThrow(() -> new RuntimeException("zone not found with id: " + p.getFk_zone_id())));
			List<PetToGroup> relationships = new ArrayList<>();
			Streamable.of(relationshipDao.getAllEntriesForGroup(p.getId())).forEach(relationships::add);
			for (PetToGroup r : relationships) {
				result.getPets().add(petDao.getPetById(r.getPet_id())
						.orElseThrow(() -> new RuntimeException("pet not found with id: " + r.getGroup_id())));
			}
			result.setNotifications(p.isNotifications());
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

			List<Pet> pets = new ArrayList<>();
			Streamable.of(petDao.getAllPetsForUser(u.getId())).forEach(pets::add);
			resultObj.setPets(pets);
		}

		resultObj.setPet_group(result);

		return resultObj;
	}

	@GetMapping("pet")
	public List<PetGroup> getGroupsForPet(@RequestParam Long pet_id) {
		List<PetToGroup> relationships = relationshipDao.getAllEntriesForPet(pet_id);
		List<PetGroup> result = new ArrayList<>();
		for (PetToGroup r : relationships) {
			Optional<PetGroup> gr = petGroupDao.getPetGroupById(r.getGroup_id());
			if (gr.isPresent()) {
				result.add(gr.get());
			}
		}
		return result;
	}

	@GetMapping("pets")
	public List<Pet> getPetsForGroup(@RequestParam Long group_id) {
		List<PetToGroup> relationships = relationshipDao.getAllEntriesForGroup(group_id);
		List<Pet> result = new ArrayList<>();
		for (PetToGroup r : relationships) {
			Optional<Pet> p = petDao.getPetById(r.getPet_id());
			if (p.isPresent()) {
				result.add(p.get());
			}
		}
		return result;
	}

	@PostMapping("save")
	public MessageResponse addPetGroup(@RequestHeader("Authorization") String token,
			@RequestBody PetGroupCreateRequest groupreq) {
		User user = getUserByToken(token);
		List<PetGroup> groups = petGroupDao.getGroupsByName(groupreq.getGroup_name());
		if (groups.size() > 0) {
			for(PetGroup g : groups) {
				if (g.getFk_user_id().equals(user.getId()) && (groupreq.getGroup_id() == null || !g.getId().equals(groupreq.getGroup_id()))) {
					return new MessageResponse(false, "Pet group name is already used.");
				}
			}
			
		}
		PetGroup group = new PetGroup();
		if (groupreq.getGroup_id() != null) {
			group.setId(groupreq.getGroup_id());
			relationshipDao.deleteAllForGroup(groupreq.getGroup_id());
		}
		group.setFk_user_id(user.getId());
		group.setName(groupreq.getGroup_name());
		if (groupreq.getZone_id() != null) {
			group.setFk_zone_id(groupreq.getZone_id());
		}
		group.setNotifications(groupreq.isNotifications());

		group = petGroupDao.save(group);
		List<PetToGroup> relationships = new ArrayList<PetToGroup>();
		List<Pet> pets = groupreq.getPets();
		for (Pet p : pets) {
			PetToGroup temp = new PetToGroup();
			temp.setGroup_id(group.getId());
			temp.setPet_id(p.getId());
			relationships.add(temp);
		}
		relationshipDao.saveAllPetToGroupRelationships(relationships);
		return new MessageResponse(true, "Pet group successfully saved!");
	}

	@DeleteMapping("delete")
	public MessageResponse deletePetGroup(@RequestHeader("Authorization") String token,
			@RequestParam Long pet_group_id) {
		Optional<PetGroup> petGroupDetails = petGroupDao.getPetGroupById(pet_group_id);
		if (petGroupDetails.isPresent()) {
			PetGroup pet_group = petGroupDetails.get();
			User user = getUserByToken(token);
			if (user.getId().equals(pet_group.getFk_user_id())) {
				eventDao.deleteForZone(pet_group_id);
				relationshipDao.deleteAllForGroup(pet_group_id);
				petGroupDao.deletePetGroup(pet_group_id);
				return new MessageResponse(true, "Pet group deleted successfully.");
			} else {
				return new MessageResponse(false, "Bad token.");

			}

		}

		return new MessageResponse(false, "Couldn't find pet group.");
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