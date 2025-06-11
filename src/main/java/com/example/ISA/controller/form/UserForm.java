package com.example.ISA.controller.form;

import com.example.ISA.groups.LoginGroup;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.example.ISA.constfolder.ErrorMessage.*;

@Getter
@Setter
public class UserForm {

    @NotEmpty(message = E0001, groups = { LoginGroup.class })
    private int id;

    @NotEmpty(message = E0002, groups = { LoginGroup.class })
    private String account;

    private String name;

    private String password;

    private int category;

    private boolean isStopped;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
