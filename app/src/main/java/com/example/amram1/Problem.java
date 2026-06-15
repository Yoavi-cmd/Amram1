package com.example.amram1;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * מחלקת מודל — מייצגת תקלה שדווחה בפנימייה.
 * IgnoreExtraProperties מונע קריסה אם ב-Firestore יש שדות שלא קיימים במחלקה.
 */
@IgnoreExtraProperties
public class Problem {
    private String typeP;         // סוג התקלה (חשמל, אינסטלציה, אינטרנט, ריהוט, אחר)
    private String id;            // מזהה ייחודי של המסמך ב-Firestore
    private int severity;         // רמת דחיפות (1=גבוהה, 2=בינונית, 3=נמוכה)
    private int roomP;            // מספר החדר
    private String description;   // תיאור התקלה
    private String imageBase64;   // תמונה מקודדת ב-Base64 (או null)
    private String reporterName;  // שם המדווח
    private String year;          // שכבת המדווח
    private String status;        // סטטוס: "active" או "fixed"
    private long timestamp;       // חותמת זמן (מילישניות)

    // בנאי ריק — חובה עבור Firebase
    public Problem() {}

    // בנאי מלא — ליצירת תקלה חדשה
    public Problem(String typeP, int severity, int roomP, String description, String imageBase64, String reporterName, String year, String status, long timestamp) {
        this.typeP = typeP;
        this.severity = severity;
        this.roomP = roomP;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.reporterName = reporterName;
        this.year = year;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters ו-Setters — Firebase משתמש בהם לקריאה/כתיבה אוטומטית
    public String getTypeP() { return typeP; }
    public void setTypeP(String typeP) { this.typeP = typeP; }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }

    public int getRoomP() { return roomP; }
    public void setRoomP(int roomP) { this.roomP = roomP; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
