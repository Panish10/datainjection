package com.ebaotech.datainjection.dev.mdbloader.repository;

import com.ebaotech.datainjection.dev.mdbloader.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query(value = "SELECT max(attendanceLogId) FROM Attendance")
    long findMaxAttendanceLogId();

    List<Attendance> findAllByEmployeeId(String employeeId);
    List<Attendance> findByAttendanceDateBetween(Date startDate, Date endDate);
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(String employeeId, Date fromData, Date toDate);
}
