package com.pettracker.pettrackerserver.pet_groups.pet_to_group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class PetToGroupDao {
	@Autowired
	private PetToGroupRepository repository;
		
	public PetToGroup save(PetToGroup pet_group) {
		return repository.save(pet_group);
	}
	
	public List<PetToGroup> getAllPetGroups() {
		List<PetToGroup> pet_to_groups = new ArrayList<>();
		Streamable.of(repository.findAll()).forEach(pet_to_groups::add);
		return pet_to_groups;
	}
		
	public List<PetToGroup> getAllEntriesForPet(Long pet_id) {
		List<PetToGroup> pet_to_groups = new ArrayList<>();
		Streamable.of(repository.getAllPetToGroupRelationshipsForPet(pet_id)).forEach(pet_to_groups::add);
		return pet_to_groups;
	}
	public List<PetToGroup> getAllEntriesForGroup(Long group_id) {
		List<PetToGroup> pet_to_groups = new ArrayList<>();
		Streamable.of(repository.getAllPetToGroupRelationshipsForGroup(group_id)).forEach(pet_to_groups::add);
		return pet_to_groups;
	}
	
	public List<PetToGroup> saveAllPetToGroupRelationships(List<PetToGroup> entries) {
		Iterable<PetToGroup> relationships = repository.saveAll(entries);
		List<PetToGroup> result = new ArrayList<>();
		Streamable.of(relationships).forEach(result::add);
		return result;
	}
	
	public void deleteAllForGroup(Long group_id) {
		repository.deleteByGroup(group_id);
	}
	
	public void deleteAllForPet(Long pet_id) {
		repository.deleteByPet(pet_id);
	}
}
