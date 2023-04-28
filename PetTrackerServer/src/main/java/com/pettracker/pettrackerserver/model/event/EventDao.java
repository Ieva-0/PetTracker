package com.pettracker.pettrackerserver.model.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class EventDao {
	@Autowired
	private EventRepository repository;
	
	public List<Event> allEvents() {
		List<Event> events = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(events::add);
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

	
}
