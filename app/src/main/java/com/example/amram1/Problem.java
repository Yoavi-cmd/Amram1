package com.example.amram1;

public class Problem {
    private String typeP;
    private int severity;
    private int roomP;
    private String reporterId; // שיניתי את השם לבהירות
    private String description; // שדה חדש לתיאור
    private String imageUrl; // שדה חדש ללינק לתמונה
    private String status; // סטטוס התקלה (active/fixed)
    private long timestamp; // זמן הדיווח

    public Problem() {
    }


    public Problem(String typeP, int severity, int roomP, String reporterId, String description, String imageUrl, String status, long timestamp) {
        this.typeP = typeP;
        this.severity = severity;
        this.roomP = roomP;
        this.reporterId = reporterId;
        this.description = description;
        this.imageUrl = imageUrl;
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

    public String getReporterId() {
        return reporterId;
    }
    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
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

}
