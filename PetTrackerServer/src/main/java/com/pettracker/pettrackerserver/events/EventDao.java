package com.pettracker.pettrackerserver.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class EventDao {
	@Autowired
	private EventRepository repository;

	public List<Event> eventsForUser(Long user_id) {
		List<Event> events = new ArrayList<>();
		Streamable.of(repository.eventsForUser(user_id)).forEach(events::add);
		return events;
	}

	public List<Event> eventsForPet(Long pet_id) {
		List<Event> events = new ArrayList<>();
		Streamable.of(repository.eventsForPet(pet_id)).forEach(events::add);
		return events;
	}

	public Event newEvent(Event entry) {
		return repository.save(entry);
	}

	public void deleteForDevice(Long device_id) {
		repository.deleteForDevice(device_id);
	}

	public void deleteForPet(Long pet_id) {
		repository.deleteForPet(pet_id);
	}

	public void deleteForZone(Long zone_id) {
		repository.deleteForZone(zone_id);
	}

	public void deleteForPetGroup(Long pet_group_id) {
		repository.deleteForPetGroup(pet_group_id);
	}

	public void deleteForUser(Long user_id) {
		repository.deleteForUser(user_id);
	}

}
