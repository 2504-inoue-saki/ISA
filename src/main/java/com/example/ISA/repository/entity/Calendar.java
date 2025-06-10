package com.example.ISA.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "working")
@Getter
@Setter
public class Calendar {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Date date;

    @Column
    private int fiscalYear;

    @Column
    private String dayOfWeek;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;
}
