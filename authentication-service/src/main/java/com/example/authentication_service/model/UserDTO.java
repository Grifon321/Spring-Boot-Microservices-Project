package com.example.authentication_service.model;

import lombok.Data;

@Data
public class UserDTO {
    private long id;
    private String username;
    private String password;
    private String email;
}
