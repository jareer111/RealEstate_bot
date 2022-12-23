package com.company.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users {
    private int id;
    private String chatId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
    private boolean isActive;
    private boolean isBlocked;
    private boolean isAdmin;
}
