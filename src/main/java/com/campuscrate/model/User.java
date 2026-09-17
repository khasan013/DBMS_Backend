package com.campuscrate.model;

public class User {

    private Long userId;
    private String studentId;
    private String name;
    private String email;
    private boolean emailVerified;
    private boolean suspended;
    private String passwordHash;
    private String phone;
    private String profileImgUrl;

    public User() {
    }

    public User(Long userId, String studentId, String name, String email, boolean emailVerified, boolean suspended, String passwordHash,
            String phone, String profileImgUrl) {
        this.userId = userId;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.emailVerified = emailVerified;
        this.suspended = suspended;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.profileImgUrl = profileImgUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }

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

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
