package com.ebaotech.datainjection.dev.mdbloader.controller;

import com.ebaotech.datainjection.dev.mdbloader.model.Attendance;
import com.ebaotech.datainjection.dev.mdbloader.repository.AttendanceRepository;
import com.ebaotech.datainjection.dev.mdbloader.service.MDBLoaderService;
import com.ebaotech.datainjection.dev.mdbloader.service.MDBLoaderServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/attendance")
public class MDBLoaderController {

    private static final Logger logger = LoggerFactory.getLogger(MDBLoaderController.class);

    @Autowired
    private MDBLoaderService mdbLoaderService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/load-mdb")
    @ApiOperation(value = "load mdb file", notes = "load mdb file and insert recored into the attendance system")
    public String loadMDBFile() {
        return mdbLoaderService.loadMDBFile();
    }

    @GetMapping
    @ApiOperation(value = "Get all attendance list", notes = "Get all attendance list")
    public List<Attendance> getAttendanceList() {
        logger.info("Retrieving all Attendance");
        return attendanceRepository.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get attendance by id", notes = "Get attendance by id")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        logger.info("Retrieving Attendance by ID: {}", id);
        return attendanceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("empId/{empId}")
    @ApiOperation(value = "Get attendance by employee id", notes = "Get attendance by employee id")
    public List<Attendance> findAllByEmployeeId(@PathVariable String empId) {
        logger.info("Retrieving Attendance by employee Id: {}", empId);
        return attendanceRepository.findAllByEmployeeId(empId);
    }

    @GetMapping("/date-range")
    @ApiOperation(value = "Get attendance by date range", notes = "get attendance by data range")
    public List<Attendance> getAllByDateRange(
            @RequestParam(required = false) String employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        if (employeeId != null) {
            logger.info("Retrieving leaves by attendance ID: {}, date range: {} to {}", employeeId, fromDate, toDate);
            return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, fromDate, toDate);
        } else {
            logger.info("Retrieving attendance within date range: {} to {}", fromDate, toDate);
            return attendanceRepository.findByAttendanceDateBetween(fromDate, toDate);
        }
    }
}