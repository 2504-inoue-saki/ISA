package com.example.ISA.controller.form;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class WorkingForm {
    private int id;

    private int userId;

    private Date date;

    private int attend;
    private String attendWord;

    private String startWork;

    private String endWork;

    private String startBreak;

    private String endBreak;

    private int status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
