package com.edusmart.dto;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String roleName;
    private int points;

    public UserResponse() {}

    public UserResponse(Long id, String username, String email, String role, String roleName, int points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.roleName = roleName;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
