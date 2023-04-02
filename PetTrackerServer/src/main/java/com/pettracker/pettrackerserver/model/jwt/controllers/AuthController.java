package com.pettracker.pettrackerserver.model.jwt.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pettracker.pettrackerserver.model.jwt.exception.TokenRefreshException;
import com.pettracker.pettrackerserver.model.jwt.payload.response.TokenRefreshResponse;
import com.pettracker.pettrackerserver.model.jwt.models.ERole;
import com.pettracker.pettrackerserver.model.jwt.models.RefreshToken;
import com.pettracker.pettrackerserver.model.jwt.models.Role;
import com.pettracker.pettrackerserver.model.jwt.models.User;
import com.pettracker.pettrackerserver.model.jwt.payload.request.LoginRequest;
import com.pettracker.pettrackerserver.model.jwt.payload.request.SignupRequest;
import com.pettracker.pettrackerserver.model.jwt.payload.request.TokenRefreshRequest;
import com.pettracker.pettrackerserver.model.jwt.payload.response.JwtResponse;
import com.pettracker.pettrackerserver.model.jwt.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.model.jwt.repository.RoleRepository;
import com.pettracker.pettrackerserver.model.jwt.repository.UserRepository;
import com.pettracker.pettrackerserver.model.jwt.security.jwt.JwtUtils;
import com.pettracker.pettrackerserver.model.jwt.security.services.UserDetailsImpl;
import com.pettracker.pettrackerserver.model.jwt.security.services.RefreshTokenService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			// This registration token comes from the client FCM SDKs.
			String registrationToken = "cnbYxhIdTYyCDL4bzuDQmV:APA91bEri00liWE__rKnAffnm8dHTut3kGFxQNjVjg4YS1gTNBXGGSPiPklpqvKl4A43e5yO-Kav8DL55PJ8sJKjx7LPbZCMAkbUnMquEeAL-agj685AtZaKns8OyZ00xl9mAtgxxuCi";

			// See documentation on defining a message payload.
//			Message message = Message.builder().setNotification(Notification.builder()
//			        .setTitle("abcd")
//			        .setBody("abc.")
//			        .build())
//					.setToken(registrationToken).build();
			
		    Notification.Builder builder = Notification.builder();
		    Message message = Message.builder()
		            .setNotification(builder.build())
		            .putData("title", "abc")
		            .putData("body", "abc")
		            .setToken(registrationToken)
		            .build();

			// Send a message to the device corresponding to the provided
			// registration token.
			String response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Successfully sent message: " + response);

		} catch (FirebaseMessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Response is a message ID string.
		if (!userRepository.existsByUsername(loginRequest.getUsername())) {
			System.out.println(loginRequest.getUsername());
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username doesn't exist!"));

		} else {
			Optional<User> userDetails = userRepository.findByUsername(loginRequest.getUsername());
			if (userDetails.isPresent()) {
				User user = userDetails.get();
				if (user.getPassword().toUpperCase().trim().equals(loginRequest.getPassword().toUpperCase().trim())) {
					String jwt = jwtUtils.generateJwtToken(user);

					List<String> roles = new ArrayList<String>();
//				    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
//				        .collect(Collectors.toList());
					System.out.println("user id" + user.getId());
					RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

					return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), user.getId(),
							user.getUsername(), user.getEmail(), roles));
				} else {
					System.out.println(user.getPassword().toUpperCase());
					System.out.println(loginRequest.getPassword());
					return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is inccorrect!"));

				}

			}

		}
		return ResponseEntity.badRequest().body(new MessageResponse("Error: login failed"));

//	    Authentication authentication = authenticationManager
//	        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//	    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtUtils.generateTokenFromUsername(user.getUsername());
					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
				})
				.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

}