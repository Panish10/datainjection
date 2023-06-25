package com.ebaotech.datainjection.dev.mdbloader.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "attendanceLogId", unique = true)
    private Long attendanceLogId;

    @Column(name = "attendanceDate")
    private Date attendanceDate;

    @Column(name = "employeeId")
    private String employeeId;

    @Column(name = "present")
    private boolean present;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttendanceLogId() {
        return attendanceLogId;
    }

    public void setAttendanceLogId(Long attendanceLogId) {
        this.attendanceLogId = attendanceLogId;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", attendanceLogId=" + attendanceLogId +
                ", attendanceDate=" + attendanceDate +
                ", employeeId='" + employeeId + '\'' +
                ", present=" + present +
                '}';
    }
}
