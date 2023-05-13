package com.pettracker.pettrackerserver.users.controllers;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pettracker.pettrackerserver.users.exception.TokenRefreshException;
import com.pettracker.pettrackerserver.users.models.RefreshToken;
import com.pettracker.pettrackerserver.users.payload.request.LoginRequest;
import com.pettracker.pettrackerserver.users.payload.request.SignupRequest;
import com.pettracker.pettrackerserver.users.payload.request.TokenRefreshRequest;
import com.pettracker.pettrackerserver.users.payload.response.JwtResponse;
import com.pettracker.pettrackerserver.users.payload.response.MessageResponse;
import com.pettracker.pettrackerserver.users.payload.response.TokenRefreshResponse;
import com.pettracker.pettrackerserver.users.repository.RoleRepository;
import com.pettracker.pettrackerserver.users.repository.UserRepository;
import com.pettracker.pettrackerserver.users.security.jwt.JwtUtils;
import com.pettracker.pettrackerserver.users.security.services.RefreshTokenService;
import com.pettracker.pettrackerserver.users.models.User;


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
	public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		if (!userRepository.existsByUsername(loginRequest.getUsername())) {
			System.out.println(loginRequest.getUsername());
			return new JwtResponse(false, "Username does not exist.");

		} else {
			Optional<User> userDetails = userRepository.findByUsername(loginRequest.getUsername());
			if (userDetails.isPresent()) {
				User user = userDetails.get();
				if (user.getPassword().toUpperCase().trim().equals(loginRequest.getPassword().toUpperCase().trim())) {
					String jwt = jwtUtils.generateJwtToken(user);
					System.out.println("user id" + user.getId());
					RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

					return new JwtResponse(jwt, refreshToken.getToken(), user.getId(),
							user.getUsername(), user.getEmail(), user.getFk_role_id(), true, "Successfully logged in!");
				} else {
					System.out.println(user.getPassword().toUpperCase());
					System.out.println(loginRequest.getPassword());
					return new JwtResponse(false, "Password is incorrect.");


				}

			}

		}
		return new JwtResponse(false, "Login failed.");
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
	public JwtResponse registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return new JwtResponse(false, "Username is taken.");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new JwtResponse(false, "Email is taken");
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getFirebase_token());
		user.setFk_role_id(3);

		User u = userRepository.save(user);
		String jwt = jwtUtils.generateJwtToken(u);
		System.out.println("user id" + u.getId());
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(u.getId());

		return new JwtResponse(jwt, refreshToken.getToken(), u.getId(),
				u.getUsername(), u.getEmail(), u.getFk_role_id(), true, "Successfully registered!");
	} 

}