package com.example.ISA.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class AllForm {
    private int id; // workingテーブルの主キー
    private String account;
    private String name;
    private int userId;

    private int attend;
    private String attendWord;

    private String startWork;
    private String endWork;
    private String startBreak;
    private String endBreak;
    private String workDuration;
    private String breakDuration;

    private int status;
    private String statusWord;

    private Date date;
    private LocalDate localDate;


    private int fiscalYear;
    private String dayOfWeek;
}
