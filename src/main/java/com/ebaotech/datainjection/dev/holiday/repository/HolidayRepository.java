package com.ebaotech.datainjection.dev.holiday.repository;

import com.ebaotech.datainjection.dev.holiday.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByDateBetween(Date startDate, Date endDate);
}
