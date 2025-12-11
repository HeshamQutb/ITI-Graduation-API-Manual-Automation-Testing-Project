package com.platzi.pojo.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private String avatar;
    private String role;

    // Constructors
    public UserRequest() {}

    public UserRequest(String name, String email, String password, String avatar, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getAvatar() { return avatar; }
    public String getRole() { return role; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setRole(String role) { this.role = role; }

    // Builder Pattern
    public static UserRequestBuilder builder() {
        return new UserRequestBuilder();
    }

    public static UserRequestBuilder defaultUser() {
        long timestamp = System.currentTimeMillis();
        return new UserRequestBuilder()
                .name("Test User")
                .email("testuser" + timestamp + "@mail.com")
                .password("testpass123")
                .avatar("https://api.lorem.space/image/face?w=640&h=480");
    }

    public static class UserRequestBuilder {
        private String name;
        private String email;
        private String password;
        private String avatar;
        private String role;

        public UserRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserRequestBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public UserRequestBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserRequest build() {
            return new UserRequest(name, email, password, avatar, role);
        }
    }
}