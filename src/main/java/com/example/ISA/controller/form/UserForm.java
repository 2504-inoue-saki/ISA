package com.example.ISA.controller.form;

import com.example.ISA.groups.AddGroup;
import com.example.ISA.groups.EditGroup;
import com.example.ISA.groups.EditPasswordGroup;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
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

    @NotEmpty(message = E0001, groups = { LoginGroup.class, AddGroup.class, EditGroup.class })
    @Size(min = 6, max = 20, message = E0004, groups = { AddGroup.class, EditGroup.class })
    @Pattern(regexp="^[0-9a-zA-Z]+$", message = E0004, groups = { AddGroup.class, EditGroup.class })
    private String account;

    @NotEmpty(message = E0002, groups = { LoginGroup.class, EditPasswordGroup.class, AddGroup.class })
    @Size(min = 6, max = 20, message = E0005, groups = {AddGroup.class, EditPasswordGroup.class})
    @Pattern(regexp = "^[!-~]+$", message = E0005, groups = {AddGroup.class, EditPasswordGroup.class})
    private String password;

    @NotEmpty(message = E0006, groups = { AddGroup.class, EditGroup.class })
    @Size(max = 10, message = E0007, groups = { AddGroup.class, EditGroup.class })
    private String name;

    private String checkPassword;

    @Min(value = 1, message = E0008, groups = { AddGroup.class, EditGroup.class })
    private Integer category; //権限系

    private Integer status; //申請系

    private Boolean stopped;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
