package com.example.amram1;

public class User {
    private String name;
    private String id;

    private int type;

    private String year;

    public User() {
    }

    public User(String name, String id, int type, String year) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }



}
