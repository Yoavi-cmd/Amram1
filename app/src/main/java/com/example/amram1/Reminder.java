package com.example.amram1;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Reminder {
    private String problemId;
    private String problemType;
    private int roomP;
    private String senderName;
    private long timestamp;

    public Reminder() {}

    public Reminder(String problemId, String problemType, int roomP, String senderName, long timestamp) {
        this.problemId = problemId;
        this.problemType = problemType;
        this.roomP = roomP;
        this.senderName = senderName;
        this.timestamp = timestamp;
    }

    public String getProblemId() { return problemId; }
    public void setProblemId(String problemId) { this.problemId = problemId; }

    public String getProblemType() { return problemType; }
    public void setProblemType(String problemType) { this.problemType = problemType; }

    public int getRoomP() { return roomP; }
    public void setRoomP(int roomP) { this.roomP = roomP; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
