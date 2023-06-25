package com.ebaotech.datainjection.dev.leave.repository;

import com.ebaotech.datainjection.dev.leave.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findAllByEmpId(String empId);
    List<Leave> findByFromDateBetweenOrToDateBetween(Date fromDate, Date toDate, Date fromDate2, Date toDate2);
    List<Leave> findByEmpIdAndFromDateBetweenOrEmpIdAndToDateBetween(String empId, Date fromDate, Date toDate, String empId2, Date fromDate2, Date toDate2);
}
