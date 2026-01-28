package com.example.amram1;

public class PermanentTask {
    private String id; // מזהה המטלה (מה-Firestore)
    private String title; // כותרת (למשל: "ניקוי פילטרים")
    private String description; // פירוט

    public PermanentTask() {} // חובה ל-Firebase

    public PermanentTask(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}