package com.example.amram1;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * מחלקת מודל — מייצגת משתמש במערכת.
 * סוגי משתמשים: 1=תלמיד, 2=מדריך, 3=אב בית, 4=מנהל.
 */
@IgnoreExtraProperties
public class User {
    private String name;  // שם המשתמש
    private String id;    // תעודת זהות — משמשת גם למזהה ייחודי וגם להתחברות
    private int type;     // סוג משתמש: 1=תלמיד, 2=מדריך, 3=אב בית, 4=מנהל
    private String year;  // שכבה

    // בנאי ריק — חובה עבור Firebase
    public User() {}

    // בנאי מלא — ליצירת משתמש חדש
    public User(String name, String id, int type, String year) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.year = year;
    }

    // Getters ו-Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
}
