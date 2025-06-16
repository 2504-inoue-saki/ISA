package com.example.ISA.repository;

import com.example.ISA.repository.entity.Working;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WorkingRepository extends JpaRepository<Working, Integer> {
    public List<Working> findByUserIdAndDateBetween(int userId, Date start, Date end);
}
