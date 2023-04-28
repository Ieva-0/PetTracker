package com.pettracker.pettrackerserver.model.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceRepository;
import com.pettracker.pettrackerserver.model.event.EventDao;
import com.pettracker.pettrackerserver.model.jwt.models.RefreshToken;
import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.response.JwtResponse;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntry;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntryDao;
import com.pettracker.pettrackerserver.model.location_entry.LocationEntryRequest;
import com.pettracker.pettrackerserver.model.location_entry.calculations.LatLng;
import com.pettracker.pettrackerserver.model.location_entry.calculations.MyLine;
import com.pettracker.pettrackerserver.model.location_entry.calculations.MyPolygon;
import com.pettracker.pettrackerserver.model.pet.Pet;
import com.pettracker.pettrackerserver.model.pet.PetRepository;
import com.pettracker.pettrackerserver.model.pet_group.PetGroup;
import com.pettracker.pettrackerserver.model.pet_group.PetGroupDao;
import com.pettracker.pettrackerserver.model.pet_group.PetToGroup;
import com.pettracker.pettrackerserver.model.pet_group.PetToGroupDao;
import com.pettracker.pettrackerserver.model.zone.Zone;
import com.pettracker.pettrackerserver.model.zone.ZoneDao;
import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;
import com.pettracker.pettrackerserver.model.zone_point.ZonePointDao;
import com.pettracker.pettrackerserver.model.event.Event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("locations")
public class LocationEntryController {
	
	@Autowired
	private LocationEntryDao locationdao;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;	
	@Autowired
	private PetRepository petRepository;
	@Autowired
	private EventDao eventdao;
	@Autowired
	private PetGroupDao groupdao;
	@Autowired
	private PetToGroupDao relationshipdao;
	@Autowired
	private ZoneDao zonedao;
	@Autowired
	private ZonePointDao zpdao;
	@GetMapping("history")
	public List<LocationEntry> locationHistoryForDevice(@RequestParam Long device_id) {
		return locationdao.locationHistoryForDevice(device_id);
	}
	
	@GetMapping("last")
	public LocationEntry lastEntryForDevice(@RequestParam Long device_id) {
		return locationdao.lastEntryForDevice(device_id);
	}
	
	@PostMapping("new")
	public ResponseEntity<?> addLocationEntryForDevice(@RequestBody LocationEntryRequest req) {
		if (!userRepository.existsByUsername(req.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username doesn't exist!"));

		} else {
			Optional<User> userDetails = userRepository.findByUsername(req.getUsername());
			Optional<Device> deviceDetails = deviceRepository.findByName(req.getDevice_name());

			if(userDetails.isPresent() && deviceDetails.isPresent()) 
			{	
				User user = userDetails.get();
				Device device = deviceDetails.get();
				
				if(!(device.getFk_user_id() == user.getId()))
					return ResponseEntity.badRequest().body(new MessageResponse("Error: device user id and user id do not match!"));
				
				if(!(device.getPassword().toUpperCase().trim().equals(req.getDevice_password().toUpperCase().trim()))) 
					return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is inccorrect!"));
				
				LocationEntry entry = new LocationEntry();
				entry.setLatitude(req.getLatitude());
				entry.setLongitude(req.getLongitude());
				entry.setFk_device_id(device.getId());
				entry.setCreated_at(System.currentTimeMillis());
				entry.setUsed_at(System.currentTimeMillis());
				LocationEntry newEntry = locationdao.newEntryForDevice(entry);
				Optional<Pet> petDetails = petRepository.getPetByDevice(device.getId());
				if(petDetails.isPresent()) {
					Pet pet = petDetails.get();
					System.out.println("pet details exist = " + pet.toString());
					List<Event> events = eventdao.eventsForPet(pet.getId());
					Collections.sort(events, (a, b) -> a.getTimestamp() < b.getTimestamp() ? -1 : a.getTimestamp().longValue() == b.getTimestamp().longValue() ? 0 : 1);
					List<PetToGroup> relationships = relationshipdao.getAllEntriesForPet(pet.getId());
					for(PetToGroup r : relationships) {
						System.out.println("pet relationship =  " +  r.toString());
						Optional<PetGroup> groupDetails = groupdao.getPetGroupById(r.getGroup_id());
						if(groupDetails.isPresent()) {
							PetGroup group = groupDetails.get();
							System.out.println("group exists =  " +  group.toString());
							if(group.getFk_zone_id() != null && group.getFk_zone_id() > 0) {
								Optional<Zone> zoneDetails = zonedao.getZoneById(group.getFk_zone_id());
								if(zoneDetails.isPresent()) {
									Zone zone = zoneDetails.get();
									System.out.println("group zone exists =  " +  zone.toString());

									System.out.println(events);
									Event latestEvent = null;
									for(Event e : events) {
										if((e.getFk_group_id() == null || group.getId() == null || e.getFk_group_id().equals(group.getId())) && e.getFk_zone_id().equals(zone.getId()) && e.getFk_device_id().equals(device.getId())) {
											
											if(latestEvent == null || latestEvent.getTimestamp() < e.getTimestamp()) {
												latestEvent = e;
											}
										}
									}
									
									if(latestEvent == null || isPetInsideZone(zone, newEntry) != (latestEvent.getFk_type() == 2)) {
										Event newE = new Event();
										newE.setFk_device_id(device.getId());
										newE.setFk_pet_id(pet.getId());
										newE.setFk_group_id(group.getId());
										newE.setFk_zone_id(zone.getId());
										newE.setFk_type(isPetInsideZone(zone, newEntry) ? 2 : 1);
										newE.setFk_user_id(user.getId());
										newE.setTimestamp(System.currentTimeMillis());
										eventdao.newEvent(newE);
										System.out.println("new event =  " +  newE.toString());
										if(!isPetInsideZone(zone, newEntry)) {
											// new notif
										}
									}
									
									
								}
							}
						}
					}
					if(pet.getFk_zone_id() != null && pet.getFk_zone_id() > 0) {

						Optional<Zone> zoneDetails = zonedao.getZoneById(pet.getFk_zone_id());
						if(zoneDetails.isPresent()) {
							Zone zone = zoneDetails.get();
							System.out.println("device individual zone =  " +  zone.toString());
							Event latestEvent = null;
							for(Event e : events) {
								if(e.getFk_group_id() == null && e.getFk_zone_id().equals(zone.getId()) && e.getFk_device_id().equals(device.getId())) {
									if(latestEvent == null || latestEvent.getTimestamp() < e.getTimestamp()) {
										latestEvent = e;
									}
								}
							}
							
							if(latestEvent == null || isPetInsideZone(zone, newEntry) != (latestEvent.getFk_type() == 2)) {
								Event newE = new Event();
								newE.setFk_device_id(device.getId());
								newE.setFk_pet_id(pet.getId());
								newE.setFk_zone_id(zone.getId());
								newE.setFk_type(isPetInsideZone(zone, newEntry) ? 2 : 1);
								newE.setFk_user_id(user.getId());
								newE.setTimestamp(System.currentTimeMillis());
								eventdao.newEvent(newE);
								System.out.println("new event =  " +  newE.toString());
								if(!isPetInsideZone(zone, newEntry)) {
									// new notif
								}
							}
							
						}
					}

					
				}
				return ResponseEntity.ok("Entry saved");
				
			}
			
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Error: entry save failed"));
				
	}
	
	public boolean isPetInsideZone(Zone z, LocationEntry e) {
		LatLng newPoint = new LatLng(e.getLatitude(), e.getLongitude());
		List<ZonePoint> points = zpdao.getAllZonePoints(z.getId());
		List<LatLng> list = new ArrayList<>();
		for(ZonePoint p : points) {
			list.add(new LatLng(p.getLatitude(), p.getLongitude()));
		}
		MyPolygon shape = new MyPolygon(list);
		return insidePolygon(newPoint, shape);
	}
    public boolean insidePolygon(LatLng p, MyPolygon shape) {
        MyLine ray = new MyLine(p, new LatLng(minlat(shape)-1, minlon(shape)-1)); 
        int intersections = 0;
        for(int i = 0; i < shape.lines.size(); i++) {
            if(doIntersect(ray, shape.lines.get(i))) {
                intersections++;
            }
        }
        if(intersections % 2 == 0) {//even
            System.out.println("outside");
            return false;
        } else { // odd
            System.out.println("inside");
            return true;
        }
    }
    public double minlat(MyPolygon p) { 
        double minlat = p.polygon_points.get(0).latitude;
        for(int i = 0; i < p.polygon_points.size(); i++) {
            if(p.polygon_points.get(i).latitude < minlat) {
                minlat = p.polygon_points.get(i).latitude;
            }
        }
        return minlat;
    }

    public double minlon(MyPolygon p) { 
        double minlon = p.polygon_points.get(0).longitude;
        for(int i = 0; i < p.polygon_points.size(); i++) {
            if(p.polygon_points.get(i).longitude < minlon) {
                minlon = p.polygon_points.get(i).longitude;
            }
        }
        return minlon;
    }
    public boolean doIntersect(MyLine l1, MyLine l2) {
        MyLine il1 = new MyLine(l1.p1, l1.p2);
        if (il1.insertIntoEquation(l2.p1) > 0 == il1.insertIntoEquation(l2.p2) > 0) {
            //does not intersect
            return false;
        }
        MyLine il2 = new MyLine(l2.p1, l2.p2);
        if (il2.insertIntoEquation(l1.p1) > 0 == il2.insertIntoEquation(l1.p2) > 0) {
            //does not intersect
            return false;
        }
        if ((il1.a * il2.b) - (il2.a * il1.b) == 0.0) {
            // collinear
            return false;
        }
        return true;
    }
}