package edu.ktu.pettrackerclient.adapter;

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

    @Override
    public String toString() {
        return "UploadResponse{" +
                "fileName='" + fileName + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
