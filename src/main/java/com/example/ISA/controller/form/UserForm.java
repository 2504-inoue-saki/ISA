package com.example.ISA.controller.form;


//import com.example.ISA.groups.AddGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    private Integer id;

    private String account;

    private String name;

//    @NotEmpty(message = E0016, groups = {AddGroup.class})
//    @Size(min = 6, max = 20, message = E0017, groups = {AddGroup.class})
//    @Pattern(regexp = "^[!-~]+$", message = E0017, groups = {AddGroup.class})
    private String password;

    private String checkPassword;

    private Integer category;

    private Integer status;

    private Boolean stopped;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
