package com.pettracker.pettrackerserver.model.controller;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.pettracker.pettrackerserver.model.device.Device;
import com.pettracker.pettrackerserver.model.device.DeviceDao;
import com.pettracker.pettrackerserver.model.device.DeviceRepository;
import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;
import com.pettracker.pettrackerserver.model.pet.FileStorageService;
import com.pettracker.pettrackerserver.model.pet.Pet;
import com.pettracker.pettrackerserver.model.pet.PetDao;
import com.pettracker.pettrackerserver.model.pet.PetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;

@RestController
@RequestMapping("pets")
public class PetController {
	
	@Autowired
	private PetDao petdao;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PetRepository petRepository;
	@Autowired
	private final FileStorageService fileStorageService;
	
	public PetController(FileStorageService fileStorageService) {
	    this.fileStorageService = fileStorageService;
	}
	
	@GetMapping("all")
	public List<Pet> getAllPets(@RequestParam Long user_id) {
		return petdao.getAllPetsForUser(user_id);
	}

	
	@PostMapping("create")
	public Pet addPet(@RequestBody Pet pet) {
		System.out.println(pet.toString());
		return petdao.save(pet);
	}
	
	@DeleteMapping("delete")
	public ResponseEntity<?> deletePet(@RequestHeader("Authorization") String token, @RequestParam Long pet_id) {
		String[] chunks = token.split("\\.");
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(chunks[1]));
		String username = payload.split("\"")[3];
		Optional<User> userDetails = userRepository.findByUsername(username);
		Optional<Pet> petDetails = petRepository.findById(pet_id);
		if(userDetails.isPresent() && petDetails.isPresent()) 
		{
			Pet pet = petDetails.get();
			User user = userDetails.get();
			if(user.getId().equals(pet.getFk_user_id())) {
				petdao.deletePet(pet_id);
				return ResponseEntity.ok().body("all good");
			}else {
				return ResponseEntity.badRequest().body(new MessageResponse("Wrong user"));

			}
			
		}
//		System.out.println(userDetails.isPresent());
//		System.out.println(deviceDetails.isPresent());
		return ResponseEntity.badRequest().body(new MessageResponse("Error"));

	}
	
	@PostMapping("edit")
	public Pet editPet(@RequestBody Pet pet) {
		return petdao.save(pet);
	}
	
} 