package com.kpi.pa.service.auth;

public class RegistrationRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;


    public RegistrationRequest(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password; 
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}