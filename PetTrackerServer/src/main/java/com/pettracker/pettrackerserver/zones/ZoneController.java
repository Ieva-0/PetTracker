package com.pettracker.pettrackerserver.zones;

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

import com.pettracker.pettrackerserver.events.EventDao;
import com.pettracker.pettrackerserver.pet_groups.PetGroup;
import com.pettracker.pettrackerserver.pet_groups.PetGroupDao;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroupDao;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroup;
import com.pettracker.pettrackerserver.pets.Pet;
import com.pettracker.pettrackerserver.pets.PetDao;
import com.pettracker.pettrackerserver.users.controllers.UserDao;
import com.pettracker.pettrackerserver.users.models.User;
import com.pettracker.pettrackerserver.users.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.zones.zone_point.ZonePoint;
import com.pettracker.pettrackerserver.zones.zone_point.ZonePointDao;

@RestController
@RequestMapping("zones")
public class ZoneController {

	@Autowired
	private ZoneDao zoneDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ZonePointDao zonePointDao;
	@Autowired
	private PetDao petDao;
	@Autowired
	private PetGroupDao groupDao;
	@Autowired
	private PetToGroupDao relationshipDao;
	@Autowired
	private EventDao eventDao;

	@GetMapping("all")
	public List<Zone> getAllZones(@RequestHeader("Authorization") String token, @RequestParam Long user_id) {
		return zoneDao.getAllZonesForUser(user_id);
	}

	@GetMapping("all_with_points")
	public ZonesForDeviceResponse getAllZonesWithPointsForDevice(@RequestHeader("Authorization") String token,
			@RequestParam Long device_id) {
		Optional<Pet> opt = petDao.getPetByDevice(device_id);
		ZonesForDeviceResponse resultObj = new ZonesForDeviceResponse();
		if (opt.isPresent()) {
			Pet p = opt.get();
			resultObj.setPet(p);
			if (p.getFk_zone_id() != null) {
				resultObj.setAssignedToPet(getZoneWithPoints(token, p.getFk_zone_id()));
			}
			List<PetToGroup> relationships = new ArrayList<>();
			Streamable.of(relationshipDao.getAllEntriesForPet(p.getId())).forEach(relationships::add);
			for (PetToGroup r : relationships) {
				Optional<PetGroup> temp = groupDao.getPetGroupById(r.getGroup_id());
				if (temp.isPresent()) {
					PetGroup gr = temp.get();
					if (gr.getFk_zone_id() != null) {
						resultObj.getGroups().add(gr);
						resultObj.getAssignedToGroups().add(getZoneWithPoints(token, gr.getFk_zone_id()));
					}
				}

			}
		}
		return resultObj;
	}

	@GetMapping("all_details")
	public List<ZoneWithDetails> getAllZonesWithDetails(@RequestHeader("Authorization") String token,
			@RequestParam Long user_id) {
		List<Zone> all = zoneDao.getAllZonesForUser(user_id);
		List<ZoneWithDetails> result = new ArrayList<ZoneWithDetails>();
		for (Zone z : all) {
			ZoneWithDetails temp = new ZoneWithDetails();
			temp.setId(z.getId());
			temp.setName(z.getName());
			temp.setFk_user_id(z.getFk_user_id());
			temp.setPets(petDao.getAllPetsByZone(z.getId()));
			temp.setGroups(groupDao.getAllPetGroupsByZone(user_id));
			result.add(temp);
		}
		return result;
	}

	@GetMapping("zone")
	public Zone getZoneById(@RequestHeader("Authorization") String token, @RequestParam Long zone_id) {
		Optional<Zone> result = zoneDao.getZoneById(zone_id);
		if (result.isPresent()) {
			return result.get();
		} else
			return new Zone();
	}

	@GetMapping("zone_with_points")
	public ZoneWithPoints getZoneWithPoints(@RequestHeader("Authorization") String token, @RequestParam Long zone_id) {
		Optional<Zone> opt = zoneDao.getZoneById(zone_id);
		ZoneWithPoints resultObj = new ZoneWithPoints();
		if (opt.isPresent()) {
			Zone z = opt.get();
			resultObj.setId(z.getId());
			resultObj.setUser_id(z.getFk_user_id());
			resultObj.setZone_name(z.getName());

			List<ZonePoint> list = zonePointDao.getAllZonePoints(zone_id);
			resultObj.setPoints(list);
		}
		return resultObj;
	}

	@PostMapping("create")
	public MessageResponse addZone(@RequestHeader("Authorization") String token, @RequestBody ZoneWithPoints zonereq) {
		User user = getUserByToken(token);
		List<Zone> zones = zoneDao.getZonesByName(zonereq.getZone_name());
		if (zones.size() > 0) {
			for(Zone z : zones) {
				if (z.getFk_user_id().equals(user.getId()) && (zonereq.getId() == null || !z.getId().equals(zonereq.getId()))) {
					System.out.println(zonereq);
					return new MessageResponse(false, "Zone name is already used.");
				}
			}
			
		}
		Zone zone = new Zone();
		if (zonereq.getId() != null) {
			zone.setId(zonereq.getId());
			zonePointDao.deleteZonePoints(zonereq.getId());
		}
		zone.setFk_user_id(user.getId());
		zone.setName(zonereq.getZone_name());
		zone = zoneDao.save(zone);
		List<ZonePoint> points = zonereq.getPoints();
		if (points.get(0).getFk_zone_id() == null) {
			for (ZonePoint p : points) {
				p.setFk_zone_id(zone.getId());
			}
		}
		zonePointDao.saveAllZonePoints(points);
		return new MessageResponse(true, "Zone saved successfully!");
	}

	@DeleteMapping("delete")
	public MessageResponse deleteZone(@RequestHeader("Authorization") String token, @RequestParam Long zone_id) {
		Optional<Zone> zoneDetails = zoneDao.getZoneById(zone_id);
		if (zoneDetails.isPresent()) {
			Zone zone = zoneDetails.get();
			User user = getUserByToken(token);
			if (user.getId() == zone.getFk_user_id()) {
				eventDao.deleteForZone(zone_id);
				zonePointDao.deleteZonePoints(zone_id);
				zoneDao.deleteZone(zone_id);
				return new MessageResponse(true, "Zone successfully deleted.");
			} else {
				return new MessageResponse(false, "Bad token.");

			}

		}
		return new MessageResponse(false, "Couldn't find zone.");

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