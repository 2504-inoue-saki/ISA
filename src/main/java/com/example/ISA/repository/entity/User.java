package com.example.ISA.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String account;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private Integer category;

    @Column(insertable = false)
    private Integer status;

    @Column(name = "is_stopped")
    private boolean isStopped;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedDate;
}
