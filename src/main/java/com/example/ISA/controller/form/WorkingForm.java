package com.example.ISA.controller.form;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class WorkingForm {
    private Integer id;

    private String account;

    private Integer userId;

    private LocalDate date;

    private Integer attend;

    private String startWork;

    private String endWork;

    private String startBreak;

    private String endBreak;

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
