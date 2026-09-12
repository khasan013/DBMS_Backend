package com.campuscrate.model;

public class Admin {

    private Long adminId;
    private String email;
    private String passwordHash;
    private String phone;
    private String profileImageUrl;

    public Admin() {
    }

    public Admin(Long adminId, String email, String passwordHash, String phone, String profileImageUrl) {
        this.adminId = adminId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}