package com.example.ISA.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "working")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Working {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer userId;

    @Column
    private LocalDate date;

    @Column
    private Integer attend;

    @Column
    private String startWork;

    @Column
    private String endWork;

    @Column
    private String startBreak;

    @Column
    private String endBreak;

    @Column
    private Integer status;

    @Column
    private String memo;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedDate;
}
