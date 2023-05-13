package com.pettracker.pettrackerserver.pet_groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class PetGroupDao {
	@Autowired
	private PetGroupRepository repository;

	public PetGroup save(PetGroup pet_group) {
		return repository.save(pet_group);
	}

	public List<PetGroup> getAllPetGroups() {
		List<PetGroup> pet_groups = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(pet_groups::add);
		return pet_groups;
	}

	public List<PetGroup> getAllPetGroupsForUser(Long user_id) {
		List<PetGroup> pet_groups = new ArrayList<>();
		Streamable.of(repository.getAllPetGroupsForUser(user_id)).forEach(pet_groups::add);
		return pet_groups;
	}

	public List<PetGroup> getAllPetGroupsByZone(Long zone_id) {
		List<PetGroup> pet_groups = new ArrayList<>();
		Streamable.of(repository.getAllPetGroupsByZone(zone_id)).forEach(pet_groups::add);
		return pet_groups;
	}

	public Optional<PetGroup> getPetGroupById(Long pet_group_id) {
		return repository.findById(pet_group_id);
	}

	public Optional<PetGroup> getPetGroupByName(String name) {
		return repository.findByName(name);
	}

	public void deletePetGroup(Long pet_group_id) {
		repository.deleteById(pet_group_id);
	}
	
	public void deletePetGroupsForUser(Long user_id) {
		repository.deleteByUser(user_id);
	}
}
