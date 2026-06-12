package com.edusmart.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UserBase extends BaseEntity {

    @Column(nullable = false)
    protected String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public abstract String getRoleName();
}
