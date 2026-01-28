package com.example.amram1;

public class Problem {
    private String typeP;
    private String id;
    private int severity;
    private int roomP;
    private String description;
    private String imageUrl;
    private String reporterName;
    private String status;
    private long timestamp;

    // Required empty public constructor for Firestore
    public Problem() {}

    public Problem(String typeP, int severity, int roomP, String description, String imageUrl, String reporterName, String status, long timestamp) {
        this.typeP = typeP;
        this.severity = severity;
        this.roomP = roomP;
        this.description = description;
        this.imageUrl = imageUrl;
        this.reporterName = reporterName;
        this.status = status;
        this.timestamp = timestamp;

    }

    public String getTypeP() {
        return typeP;
    }

    public void setTypeP(String typeP) {
        this.typeP = typeP;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getRoomP() {
        return roomP;
    }

    public void setRoomP(int roomP) {
        this.roomP = roomP;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getId() {return id;}

    public void setId(String id) { this.id = id;}


}
