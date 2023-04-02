package com.pettracker.pettrackerserver.model.pet;

public class UploadResponse {
  private final String fileName;
  private final String fullName;

  public UploadResponse(String fileName, String fullName) {
    this.fileName = fileName;
    this.fullName = fullName;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFullName() {
    return fullName;
  }
}