package com.pettracker.pettrackerserver.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettracker.pettrackerserver.devices.DeviceRepository;
import com.pettracker.pettrackerserver.pet_groups.PetGroupDao;
import com.pettracker.pettrackerserver.pets.PetDao;
import com.pettracker.pettrackerserver.zones.ZoneDao;

@RestController
@RequestMapping("events")
public class EventController {

	@Autowired
	private EventDao eventDao;
	@Autowired
	private PetDao petDao;
	@Autowired
	private DeviceRepository deviceDao;
	@Autowired
	private ZoneDao zoneDao;
	@Autowired
	private PetGroupDao groupDao;

	@GetMapping("all_details")
	public List<EventWithDetails> getAllEventsWithDetails(@RequestHeader("Authorization") String token,
			@RequestParam Long user_id) {
		List<Event> all = eventDao.eventsForUser(user_id);
		List<EventWithDetails> resultList = new ArrayList<EventWithDetails>();
		for (Event e : all) {
			EventWithDetails temp = new EventWithDetails();
			temp.setId(e.getId());
			temp.setType(e.getFk_type());
			temp.setTimestamp(e.getTimestamp());
			temp.setFk_user_id(e.getFk_user_id());
			if (e.getFk_device_id() != null)
				temp.setDevice(deviceDao.findById(e.getFk_device_id())
						.orElseThrow(() -> new RuntimeException("device not found with id: " + e.getFk_device_id())));
			if (e.getFk_pet_id() != null)
				temp.setPet(petDao.getPetById(e.getFk_pet_id())
						.orElseThrow(() -> new RuntimeException("pet not found with id: " + e.getFk_pet_id())));
			if (e.getFk_zone_id() != null)
				temp.setZone(zoneDao.getZoneById(e.getFk_zone_id())
						.orElseThrow(() -> new RuntimeException("zone not found with id: " + e.getFk_zone_id())));
			if (e.getFk_group_id() != null)
				temp.setPet_group(groupDao.getPetGroupById(e.getFk_group_id())
						.orElseThrow(() -> new RuntimeException("pet group not found with id: " + e.getFk_group_id())));

			resultList.add(temp);
		}
		System.out.println(resultList);
		return resultList;
	}

}