package com.pettracker.pettrackerserver.users.payload.response;

public class MessageResponse {
	private boolean successful;
	private String message;

	public MessageResponse(boolean successful, String message) {
		this.successful = successful;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
