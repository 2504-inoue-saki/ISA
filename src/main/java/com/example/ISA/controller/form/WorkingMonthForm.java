package com.example.ISA.controller.form;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkingMonthForm {
    private Integer userId;
    private int year;
    private int month;
    @Valid
    private List<WorkingForm> workings; // WorkingFormのリストを持つ

    public WorkingMonthForm() {
        // デフォルトコンストラクタ
    }

    public WorkingMonthForm(int year, int month, List<WorkingForm> workings) {
        this.year = year;
        this.month = month;
        this.workings = workings;
    }
}