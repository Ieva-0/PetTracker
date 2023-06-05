package com.pettracker.pettrackerserver.pets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class PetDao {
	@Autowired
	private PetRepository repository;
	
	public Pet save(Pet pet) {
		return repository.save(pet);
	}
	
	public List<Pet> getAllPets() {
		List<Pet> pets = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(pets::add);
		return pets;
	}
		
	public List<Pet> getAllPetsForUser(Long user_id) {
		List<Pet> pets = new ArrayList<>();
		Streamable.of(repository.getAllPetsForUser(user_id)).forEach(pets::add);
		return pets;
	}
	
	public Optional<Pet> getPetById(Long pet_id) {
		return repository.findById(pet_id);
	}
	
	public List<Pet> getPetByName(String name) {
		List<Pet> pets = new ArrayList<>();
		Streamable.of(repository.findByName(name)).forEach(pets::add);
		return pets;
	}
	
	public Optional<Pet> getPetByDevice(Long device_id) {
		return repository.getPetByDevice(device_id);
	}
	
	public List<Pet> getAllPetsByZone(Long zone_id) {
		List<Pet> pets = new ArrayList<>();
		Streamable.of(repository.getAllPetsByZone(zone_id)).forEach(pets::add);
		return pets;
	}

	public void deletePet(Long pet_id) {
		repository.deleteById(pet_id);
	}
	public void deletePetsForUser(Long user_id) {
		repository.deleteByUser(user_id);
	}
}
