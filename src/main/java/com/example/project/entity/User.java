package com.example.project.entity;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
private int id;
@Email
private String email;
private String password;
private String gender;
private String nickname;
private String image;
private LocalDate createdAt;
private String provider;
private String providerId;
private String picture;
}
