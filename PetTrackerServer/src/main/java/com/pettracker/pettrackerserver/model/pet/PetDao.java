package com.pettracker.pettrackerserver.model.pet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.pettracker.pettrackerserver.model.pet_group.PetToGroupRepository;
import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;


@Service
public class PetDao {
	@Autowired
	private PetRepository repository;
	@Autowired 
	private PetToGroupRepository pettogrouprepo;
	
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

	public void deletePet(Long pet_id) {
		pettogrouprepo.deletePetToGroupRelationshipsForPet(pet_id);
		repository.deleteById(pet_id);
		
	}
	
}
