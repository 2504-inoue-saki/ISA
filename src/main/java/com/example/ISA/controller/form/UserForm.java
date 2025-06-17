package com.example.ISA.controller.form;

import com.example.ISA.groups.EditPasswordGroup;
import jakarta.validation.constraints.NotEmpty;
import com.example.ISA.groups.LoginGroup;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


import static com.example.ISA.constfolder.ErrorMessage.*;

@Getter
@Setter
public class UserForm {

    private Integer id;

    @NotEmpty(message = E0001, groups = { LoginGroup.class })
    private String account;

    private String name;

    @NotEmpty(message = E0016, groups = {EditPasswordGroup.class})
    @Size(min = 6, max = 20, message = E0017, groups = {EditPasswordGroup.class})
    @Pattern(regexp = "^[!-~]+$", message = E0017, groups = {EditPasswordGroup.class})
    @NotEmpty(message = E0002, groups = { LoginGroup.class })
    private String password;

    private String checkPassword;

    private Integer category;

    private Integer status;

    private Boolean stopped;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
