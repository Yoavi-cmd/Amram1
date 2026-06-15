package com.example.amram1;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * מחלקת מודל — מייצגת משימת קבע בפנימייה.
 * משימות קבע נוצרות על ידי אב הבית ומוצגות לתלמידים.
 */
@IgnoreExtraProperties
public class PermanentTask {
    private String id;          // מזהה המשימה מ-Firestore
    private String title;       // כותרת המשימה
    private String description; // תיאור מפורט

    // בנאי ריק — חובה עבור Firebase
    public PermanentTask() {}

    // בנאי עם פרמטרים
    public PermanentTask(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters ו-Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
