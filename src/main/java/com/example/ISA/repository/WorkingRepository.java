package com.example.ISA.repository;

import com.example.ISA.repository.entity.Working;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingRepository extends JpaRepository<Working, Integer> {
}
