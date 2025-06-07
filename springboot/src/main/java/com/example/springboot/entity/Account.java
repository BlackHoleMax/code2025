package com.example.springboot.entity;

import lombok.Data;

@Data
public class Account {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;
    private String role;
    private String token;
    private String avatar;
    private String newPassword;
}
