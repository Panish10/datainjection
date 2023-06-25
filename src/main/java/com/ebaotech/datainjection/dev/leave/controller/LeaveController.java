package com.ebaotech.datainjection.dev.leave.controller;

import com.ebaotech.datainjection.dev.leave.model.Leave;
import com.ebaotech.datainjection.dev.leave.repository.LeaveRepository;
import com.ebaotech.datainjection.dev.leave.service.LeaveService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/leaves")
public class LeaveController {

    private static final Logger logger = LoggerFactory.getLogger(LeaveController.class);

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    LeaveService leaveService;

    private String step = null;

    @PostMapping("/upload")
    @ApiOperation(value = "Upload Leave", notes = "Upload excel file of Leave list")
    public String uploadLeaveSheet(@RequestParam("file") MultipartFile file) {
        return leaveService.uploadLeaveSheet(file);
    }

    @GetMapping
    @ApiOperation(value = "Get all leaves", notes = "Get all leaves without any criteria")
    public List<Leave> getAllLeaves() {
        logger.info("Retrieving all leaves");
        return leaveRepository.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get leave by Id", notes = "Get leave by Id")
    public ResponseEntity<Leave> getLeaveById(@PathVariable Long id) {
        logger.info("Retrieving leave by ID: {}", id);
        return leaveRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("empId/{empId}")
    @ApiOperation(value = "Get leave by employee Id", notes = "Get leave by employee Id")
    public List<Leave> getLeaveByEmpId(@PathVariable String empId) {
        logger.info("Retrieving leave by employe Id: {}", empId);
        return leaveRepository.findAllByEmpId(empId);
    }

    @GetMapping("/date-range")
    @ApiOperation(value = "Get leave by date range", notes = "get leave by data range")
    public List<Leave> getAllByDateRange(
            @RequestParam(required = false) String empId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        if (empId != null) {
            logger.info("Retrieving leaves by employee ID: {}, date range: {} to {}", empId, fromDate, toDate);
            return leaveRepository.findByEmpIdAndFromDateBetweenOrEmpIdAndToDateBetween(empId, fromDate, toDate, empId, fromDate, toDate);
        } else {
            logger.info("Retrieving leaves within date range: {} to {}", fromDate, toDate);
            return leaveRepository.findByFromDateBetweenOrToDateBetween(fromDate, toDate, fromDate, toDate);
        }
    }

    @PostMapping
    @ApiOperation(value = "Insert leave for employee", notes = "Insert leave for employee")
    public Leave createLeave(@RequestBody Leave leave) {
        logger.info("Creating a new leave");
        return leaveRepository.save(leave);
    }

    @PutMapping
    @ApiOperation(value = "Update existing leave", notes = "Update existing leave")
    public ResponseEntity<Leave> updateLeave(@RequestBody Leave leave) {
        logger.info("Updating leave: {}", leave);
        if (!leaveRepository.existsById(leave.getId())) {
            return ResponseEntity.notFound().build();
        }

        Leave updated = leaveRepository.save(leave);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete existing leave", notes = "Delete existing leave")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        logger.info("Deleting leave by ID: {}", id);
        if (!leaveRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
