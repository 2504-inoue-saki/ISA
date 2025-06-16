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
public class Working {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int userId;

    @Column
    private Date date;

    @Column
    private int attend;

    @Column
    private String startWork;

    @Column
    private String endWork;

    @Column
    private String startBreak;

    @Column
    private String endBreak;

    @Column
    private int status;

    @Column
    private String memo;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;
}
