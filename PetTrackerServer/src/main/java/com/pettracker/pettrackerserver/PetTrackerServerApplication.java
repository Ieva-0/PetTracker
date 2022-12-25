package com.pettracker.pettrackerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.pettracker.pettrackerserver")
@EnableAutoConfiguration
public class PetTrackerServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetTrackerServerApplication.class, args);
	}

}
