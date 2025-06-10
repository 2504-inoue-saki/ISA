package com.example.ISA.controller.form;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserForm {

    private int id;

    private String account;

    private String name;

    private String password;

    private int category;

    private boolean isStopped;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
