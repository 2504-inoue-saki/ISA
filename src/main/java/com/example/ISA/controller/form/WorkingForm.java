package com.example.ISA.controller.form;

import com.example.ISA.groups.AddGroup;
import com.example.ISA.groups.EditPasswordGroup;
import com.example.ISA.groups.LoginGroup;
import com.example.ISA.groups.PopupGroup;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static com.example.ISA.constfolder.ErrorMessage.*;

@Getter
@Setter
public class WorkingForm {
    private Integer id;

    private String account;

    private Integer userId;

    private LocalDate date;

    private Integer attend;

    @NotEmpty(message = E0011, groups = {PopupGroup.class})
    @Size(min = 3, max = 5, message = E0015, groups = {PopupGroup.class})
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$", message = E0015, groups = {PopupGroup.class})
    private String startWork;

    @NotEmpty(message = E0012, groups = {PopupGroup.class})
    @Size(min = 3, max = 5, message = E0015, groups = {PopupGroup.class})
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$", message = E0015, groups = {PopupGroup.class})
    private String endWork;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$|^$", message = E0009)
    private String startBreak;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$|^$", message = E0009)
    private String endBreak;

    @Size(max = 100, message = E0016, groups = {PopupGroup.class})

    private String memo;

    private Integer status;

    // 曜日
    private String dayOfWeek;
    // 休憩時間
    private String breakTimeDisplay;
    // 労働時間
    private String workingTimeDisplay;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
