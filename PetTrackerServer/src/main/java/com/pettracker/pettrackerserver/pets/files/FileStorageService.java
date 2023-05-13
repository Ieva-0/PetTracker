package com.pettracker.pettrackerserver.pets.files;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(Environment env) {
		this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir", "./files")).toAbsolutePath()
				.normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	public String storeFile(byte[] file) {
		// Normalize file name
		String fileName = new Date().getTime() + ".txt";
		System.out.println("filename " + fileName);
		try {
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			System.out.println("path " + targetLocation);
			InputStream stream = new ByteArrayInputStream(file);
			Files.copy(stream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException ex) {
			throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public byte[] getFile(String fileName) {
		try {
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			System.out.println("path " + targetLocation);
			byte[] photo = Files.readAllBytes(targetLocation);
			return photo;

		} catch (IOException ex) {
			throw new RuntimeException("Could not retrieve file " + fileName + ". Please try again!", ex);
		}
	}

	public void deleteFile(String fileName) {
		try {
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			System.out.println("deleting path " + targetLocation);
			Files.delete(targetLocation);
		} catch (IOException ex) {
			throw new RuntimeException("Could not delete file " + fileName + ". Please try again!", ex);
		}
	}

}