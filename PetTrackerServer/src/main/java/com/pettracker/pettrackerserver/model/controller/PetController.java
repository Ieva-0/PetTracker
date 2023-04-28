package com.pettracker.pettrackerserver.model.controller;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.*;

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
import com.pettracker.pettrackerserver.model.pet.PetWithPhoto;
import com.pettracker.pettrackerserver.model.pet.UploadResponse;

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
	public List<PetWithPhoto> getAllPets(@RequestParam Long user_id) {
		List<Pet> allPets = petdao.getAllPetsForUser(user_id);
		List<PetWithPhoto> resultList = new ArrayList<PetWithPhoto>();
		for(Pet p : allPets) {
			PetWithPhoto temp = new PetWithPhoto();
			temp.setId(p.getId());
			temp.setName(p.getName());
			temp.setFk_device_id(p.getFk_device_id());
			temp.setFk_user_id(p.getFk_user_id());
			temp.setFk_zone_id(p.getFk_zone_id());
			temp.setNotifications(p.isNotifications());
			if(p.getPhoto() != null)
			{
				byte[] picture = fileStorageService.getFile(p.getPhoto());
				temp.setPicture(Base64.getEncoder().encodeToString(picture));
				
			}
			resultList.add(temp);
		}
		System.out.println(resultList);
		return resultList;
	}
	
	@GetMapping("all_nophoto")
	public List<Pet> getAllPetsWithoutPhotos(@RequestParam Long user_id) {
		return petdao.getAllPetsForUser(user_id);
	}

	@GetMapping("pet")
	public PetWithPhoto getDeviceById(@RequestHeader("Authorization") String token, @RequestParam Long pet_id) {
		Optional<Pet> result = petdao.getPetById(pet_id);
		if(result.isPresent()) {
			Pet p = result.get();
			PetWithPhoto temp = new PetWithPhoto();
			temp.setId(p.getId());
			temp.setName(p.getName());
			temp.setFk_device_id(p.getFk_device_id());
			temp.setFk_user_id(p.getFk_user_id());
			temp.setFk_zone_id(p.getFk_zone_id());
			temp.setNotifications(p.isNotifications());
			if(p.getPhoto() != null)
			{
				byte[] picture = fileStorageService.getFile(p.getPhoto());
				temp.setPicture(Base64.getEncoder().encodeToString(picture));
				
			}
			return temp;
		}
		else return new PetWithPhoto();
	}
	@PostMapping("create")
	public Pet addPet(@RequestBody PetWithPhoto petwithphoto) {
		Pet pet = new Pet();
		if(petwithphoto.getId() != null)
			pet.setId(petwithphoto.getId());
		pet.setName(petwithphoto.getName());
		pet.setFk_device_id(petwithphoto.getFk_device_id());
		pet.setFk_user_id(petwithphoto.getFk_user_id());
		pet.setFk_zone_id(petwithphoto.getFk_zone_id());
		pet.setNotifications(petwithphoto.isNotifications());
		if(petwithphoto.getPicture() != null)
		{
			String fileName = fileStorageService.storeFile(Base64.getDecoder().decode(petwithphoto.getPicture()));
			pet.setPhoto(fileName);

		}
//	    UploadResponse uploadResponse = new UploadResponse(fileName, "name");
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
				fileStorageService.deleteFile(pet.getPhoto());
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