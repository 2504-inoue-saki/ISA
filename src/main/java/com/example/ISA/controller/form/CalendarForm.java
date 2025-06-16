package com.example.ISA.controller.form;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class CalendarForm {
    private LocalDate date;

    private int fiscalYear;

    private String dayOfWeek;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
