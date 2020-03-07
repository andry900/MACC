package com.example.macc;

public class Users {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String date_birth;
    private String university;
    private String department;

    Users(String id, String name, String surname, String email, String date_birth, String university, String department) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.date_birth = date_birth;
        this.university = university;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate_birth() {
        return date_birth;
    }

    public void setDate_birth(String date_birth) {
        this.date_birth = date_birth;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
