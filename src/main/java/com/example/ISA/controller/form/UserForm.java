package com.example.ISA.controller.form;

//import com.example.ISA.groups.AddGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.example.ISA.constfolder.ErrorMessage.E0016;
import static com.example.ISA.constfolder.ErrorMessage.E0017;

@Getter
@Setter
public class UserForm {

    private int id;

    private String account;

    private String name;

//    @NotEmpty(message = E0016, groups = {AddGroup.class})
//    @Size(min = 6, max = 20, message = E0017, groups = {AddGroup.class})
//    @Pattern(regexp = "^[!-~]+$", message = E0017, groups = {AddGroup.class})
    private String password;

    private int category;

    private boolean isStopped;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private String checkPassword;
}
