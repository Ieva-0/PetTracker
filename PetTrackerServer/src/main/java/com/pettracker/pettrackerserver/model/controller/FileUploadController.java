package com.pettracker.pettrackerserver.model.controller;

import com.pettracker.pettrackerserver.model.pet.UploadResponse;
import com.pettracker.pettrackerserver.model.pet.FileStorageService;

import java.io.InputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class FileUploadController {

  private final FileStorageService fileStorageService;

  public FileUploadController(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  @PostMapping("/upload")
  public ResponseEntity<UploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file
//      @RequestParam("fullName") String fullName
  ) {
      System.out.println("inside request");

      String fileName = fileStorageService.storeFile(file);
      System.out.println("filename controller " + fileName);

      UploadResponse uploadResponse = new UploadResponse(fileName, "name");
      System.out.println("upload resp " + uploadResponse);

      return ResponseEntity.ok().body(uploadResponse);
  }
  
  @PostMapping("/array")
  public ResponseEntity<UploadResponse> uploadArray(
	      @RequestBody byte[] file
	  ) {
	      System.out.println("inside request");
	      String fileName = fileStorageService.storeFile(file);
	      System.out.println("filename controller " + fileName);
	      UploadResponse uploadResponse = new UploadResponse(fileName, "name");
	      System.out.println("upload resp " + uploadResponse);

	      return ResponseEntity.ok().body(uploadResponse);
	  }
  @GetMapping("/array")
  public ResponseEntity<byte[]> getArray(
	      @RequestParam String filename
	  ) {
	      System.out.println("inside request");
	      byte[] photo = fileStorageService.getFile(filename);
	      return ResponseEntity.ok().body(photo);
	  }
  @PostMapping("/stream")
  public ResponseEntity<UploadResponse> uploadStream(
	      @RequestBody InputStream file
//	      @RequestParam("fullName") String fullName
	  ) {
	      System.out.println("inside request");

	      String fileName = fileStorageService.storeFile(file);
	      System.out.println("filename controller " + fileName);

	      UploadResponse uploadResponse = new UploadResponse(fileName, "name");
	      System.out.println("upload resp " + uploadResponse);

	      return ResponseEntity.ok().body(uploadResponse);
	  }
}
