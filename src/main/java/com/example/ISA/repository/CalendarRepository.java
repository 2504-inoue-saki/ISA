package com.example.ISA.repository;

import com.example.ISA.repository.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, LocalDate> {

    Optional<Calendar> findByDate(LocalDate date);

    List<Calendar> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);
}
