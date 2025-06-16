package com.example.ISA.controller.form;



import com.example.ISA.groups.EditPasswordGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotEmpty(message = E0016, groups = {EditPasswordGroup.class})
    @Size(min = 6, max = 20, message = E0017, groups = {EditPasswordGroup.class})
    @Pattern(regexp = "^[!-~]+$", message = E0017, groups = {EditPasswordGroup.class})
    private String password;

    private int category;

    private boolean isStopped;

    private int status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private String checkPassword;
}
