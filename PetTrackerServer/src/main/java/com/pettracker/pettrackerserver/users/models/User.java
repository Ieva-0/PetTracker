package com.pettracker.pettrackerserver.users.models;

import javax.persistence.*;


@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String email;
	private String password;
	private Integer fk_role_id;
	private boolean blocked;
    private String firebase_token;

	public User() {
	}

	public User(String username, String email, String password, String firebase_token) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.firebase_token = firebase_token;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getFk_role_id() {
		return fk_role_id;
	}

	public void setFk_role_id(Integer fk_role_id) {
		this.fk_role_id = fk_role_id;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

    public String getFirebase_token() {
        return firebase_token;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }
}