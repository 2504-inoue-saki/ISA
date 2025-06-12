package com.example.ISA.Dto;

import lombok.Getter;
import lombok.Setter;

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
    private int status;
    private String statusWord;
    private Date date;
    private int fiscalYear;
    private String dayOfWeek;
}
