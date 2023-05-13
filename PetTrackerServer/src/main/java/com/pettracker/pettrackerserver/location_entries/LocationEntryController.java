package com.pettracker.pettrackerserver.location_entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettracker.pettrackerserver.devices.Device;
import com.pettracker.pettrackerserver.devices.DeviceDao;
import com.pettracker.pettrackerserver.events.Event;
import com.pettracker.pettrackerserver.events.EventDao;
import com.pettracker.pettrackerserver.location_entries.calculations.LatLng;
import com.pettracker.pettrackerserver.location_entries.calculations.MyLine;
import com.pettracker.pettrackerserver.location_entries.calculations.MyPolygon;
import com.pettracker.pettrackerserver.pet_groups.PetGroup;
import com.pettracker.pettrackerserver.pet_groups.PetGroupDao;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroup;
import com.pettracker.pettrackerserver.pet_groups.pet_to_group.PetToGroupDao;
import com.pettracker.pettrackerserver.pets.Pet;
import com.pettracker.pettrackerserver.pets.PetDao;
import com.pettracker.pettrackerserver.users.controllers.UserDao;
import com.pettracker.pettrackerserver.users.models.User;
import com.pettracker.pettrackerserver.users.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.zones.Zone;
import com.pettracker.pettrackerserver.zones.ZoneDao;
import com.pettracker.pettrackerserver.zones.zone_point.ZonePoint;
import com.pettracker.pettrackerserver.zones.zone_point.ZonePointDao;

@RestController
@RequestMapping("locations")
public class LocationEntryController {

	@Autowired
	private LocationEntryDao locationEntryDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private PetDao petDao;
	@Autowired
	private EventDao eventDao;
	@Autowired
	private PetGroupDao petGroupDao;
	@Autowired
	private PetToGroupDao relationshipDao;
	@Autowired
	private ZoneDao zoneDao;
	@Autowired
	private ZonePointDao zonePointDao;

	@GetMapping("history")
	public List<LocationEntry> locationHistoryForDevice(@RequestParam Long device_id) {
		return locationEntryDao.locationHistoryForDevice(device_id);
	}

	@GetMapping("last")
	public LocationEntry lastEntryForDevice(@RequestParam Long device_id) {
		return locationEntryDao.lastEntryForDevice(device_id);
	}

	@PostMapping("new")
	public MessageResponse addLocationEntryForDevice(@RequestBody LocationEntryRequest req) {
		if (!userDao.existsByUsername(req.getUsername())) {
			return new MessageResponse(false, "Error: Username doesn't exist!");

		} else {
			Optional<User> userDetails = userDao.getByUsername(req.getUsername());
			Optional<Device> deviceDetails = deviceDao.getDeviceByName(req.getDevice_name());

			if (userDetails.isPresent() && deviceDetails.isPresent()) {
				User user = userDetails.get();
				Device device = deviceDetails.get();

				if (!(device.getFk_user_id() == user.getId()))
					return new MessageResponse(false, "Error: device user id and user id do not match!");

				if (!(device.getPassword().toUpperCase().trim().equals(req.getDevice_password().toUpperCase().trim())))
					return new MessageResponse(false, "Error: Password is inccorrect!");

				LocationEntry entry = new LocationEntry();
				entry.setLatitude(req.getLatitude());
				entry.setLongitude(req.getLongitude());
				entry.setFk_device_id(device.getId());
				entry.setCreated_at(System.currentTimeMillis());
				entry.setUsed_at(System.currentTimeMillis());
				LocationEntry newEntry = locationEntryDao.newEntryForDevice(entry);
				Optional<Pet> petDetails = petDao.getPetByDevice(device.getId());
				if (petDetails.isPresent()) {
					Pet pet = petDetails.get();
					System.out.println("pet details exist = " + pet.toString());
					List<Event> events = eventDao.eventsForPet(pet.getId());
					Collections.sort(events, (a, b) -> a.getTimestamp() < b.getTimestamp() ? -1
							: a.getTimestamp().longValue() == b.getTimestamp().longValue() ? 0 : 1);
					List<PetToGroup> relationships = relationshipDao.getAllEntriesForPet(pet.getId());
					for (PetToGroup r : relationships) {
						System.out.println("pet relationship =  " + r.toString());
						Optional<PetGroup> groupDetails = petGroupDao.getPetGroupById(r.getGroup_id());
						if (groupDetails.isPresent()) {
							PetGroup group = groupDetails.get();
							System.out.println("group exists =  " + group.toString());
							if (group.getFk_zone_id() != null && group.getFk_zone_id() > 0) {
								Optional<Zone> zoneDetails = zoneDao.getZoneById(group.getFk_zone_id());
								if (zoneDetails.isPresent()) {
									Zone zone = zoneDetails.get();
									System.out.println("group zone exists =  " + zone.toString());

									System.out.println(events);
									Event latestEvent = null;
									for (Event e : events) {
										if ((e.getFk_group_id() == null || group.getId() == null
												|| e.getFk_group_id().equals(group.getId()))
												&& e.getFk_zone_id().equals(zone.getId())
												&& e.getFk_device_id().equals(device.getId())) {

											if (latestEvent == null || latestEvent.getTimestamp() < e.getTimestamp()) {
												latestEvent = e;
											}
										}
									}

									if (latestEvent == null
											|| isPetInsideZone(zone, newEntry) != (latestEvent.getFk_type() == 2)) {
										Event newE = new Event();
										newE.setFk_device_id(device.getId());
										newE.setFk_pet_id(pet.getId());
										newE.setFk_group_id(group.getId());
										newE.setFk_zone_id(zone.getId());
										newE.setFk_type(isPetInsideZone(zone, newEntry) ? 2 : 1);
										newE.setFk_user_id(user.getId());
										newE.setTimestamp(System.currentTimeMillis());
										eventDao.newEvent(newE);
										System.out.println("new event =  " + newE.toString());
										if (!isPetInsideZone(zone, newEntry)) {
											// new notif
										}
									}

								}
							}
						}
					}
					if (pet.getFk_zone_id() != null && pet.getFk_zone_id() > 0) {

						Optional<Zone> zoneDetails = zoneDao.getZoneById(pet.getFk_zone_id());
						if (zoneDetails.isPresent()) {
							Zone zone = zoneDetails.get();
							System.out.println("device individual zone =  " + zone.toString());
							Event latestEvent = null;
							for (Event e : events) {
								if (e.getFk_group_id() == null && e.getFk_zone_id().equals(zone.getId())
										&& e.getFk_device_id().equals(device.getId())) {
									if (latestEvent == null || latestEvent.getTimestamp() < e.getTimestamp()) {
										latestEvent = e;
									}
								}
							}

							if (latestEvent == null
									|| isPetInsideZone(zone, newEntry) != (latestEvent.getFk_type() == 2)) {
								Event newE = new Event();
								newE.setFk_device_id(device.getId());
								newE.setFk_pet_id(pet.getId());
								newE.setFk_zone_id(zone.getId());
								newE.setFk_type(isPetInsideZone(zone, newEntry) ? 2 : 1);
								newE.setFk_user_id(user.getId());
								newE.setTimestamp(System.currentTimeMillis());
								eventDao.newEvent(newE);
								System.out.println("new event =  " + newE.toString());
								if (!isPetInsideZone(zone, newEntry)) {
									// new notif
								}
							}

						}
					}

				}
				return new MessageResponse(true, "Entry saved");

			}

		}
		return new MessageResponse(false, "Error: couldn't save entry.");

	}

	public boolean isPetInsideZone(Zone z, LocationEntry e) {
		LatLng newPoint = new LatLng(e.getLatitude(), e.getLongitude());
		List<ZonePoint> points = zonePointDao.getAllZonePoints(z.getId());
		List<LatLng> list = new ArrayList<>();
		for (ZonePoint p : points) {
			list.add(new LatLng(p.getLatitude(), p.getLongitude()));
		}
		MyPolygon shape = new MyPolygon(list);
		return insidePolygon(newPoint, shape);
	}

	public boolean insidePolygon(LatLng p, MyPolygon shape) {
		MyLine ray = new MyLine(p, new LatLng(minlat(shape) - 1, minlon(shape) - 1));
		int intersections = 0;
		for (int i = 0; i < shape.lines.size(); i++) {
			if (doIntersect(ray, shape.lines.get(i))) {
				intersections++;
			}
		}
		if (intersections % 2 == 0) {// even
			System.out.println("outside");
			return false;
		} else { // odd
			System.out.println("inside");
			return true;
		}
	}

	public double minlat(MyPolygon p) {
		double minlat = p.polygon_points.get(0).latitude;
		for (int i = 0; i < p.polygon_points.size(); i++) {
			if (p.polygon_points.get(i).latitude < minlat) {
				minlat = p.polygon_points.get(i).latitude;
			}
		}
		return minlat;
	}

	public double minlon(MyPolygon p) {
		double minlon = p.polygon_points.get(0).longitude;
		for (int i = 0; i < p.polygon_points.size(); i++) {
			if (p.polygon_points.get(i).longitude < minlon) {
				minlon = p.polygon_points.get(i).longitude;
			}
		}
		return minlon;
	}

	public boolean doIntersect(MyLine l1, MyLine l2) {
		MyLine il1 = new MyLine(l1.p1, l1.p2);
		if (il1.insertIntoEquation(l2.p1) > 0 == il1.insertIntoEquation(l2.p2) > 0) {
			// does not intersect
			return false;
		}
		MyLine il2 = new MyLine(l2.p1, l2.p2);
		if (il2.insertIntoEquation(l1.p1) > 0 == il2.insertIntoEquation(l1.p2) > 0) {
			// does not intersect
			return false;
		}
		if ((il1.a * il2.b) - (il2.a * il1.b) == 0.0) {
			// collinear
			return false;
		}
		return true;
	}
}