package com.ebaotech.datainjection.dev.mdbloader.service;

import com.ebaotech.datainjection.dev.mdbloader.model.Attendance;
import com.ebaotech.datainjection.dev.mdbloader.repository.AttendanceRepository;
import com.ebaotech.datainjection.dev.util.DateUtil;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MDBLoaderServiceImpl implements MDBLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(MDBLoaderServiceImpl.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Value("${mdb.filePath}")
    private String filePath;

    @Value("${mdb.dbName}")
    private String mdbDBName;

    @Value("${mdb.dbFullPath}")
    private String dbFullPath;

    @Value("${mdb.tableName}")
    private String tableName;

    public String loadMDBFile() {

        logger.info("Fetching data from ");
        String message = null;
        long maxAttendanceLogId = 0;
        logger.info("Reading mdb file from ", dbFullPath);
        try (Database db = DatabaseBuilder.open(new File(dbFullPath))) {

            logger.info("Table from mdb db " +tableName);
            Table table = db.getTable(tableName);
            try {
                maxAttendanceLogId = attendanceRepository.findMaxAttendanceLogId();
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            logger.info("Max attendanceLogId is " + maxAttendanceLogId);

            List<Attendance> attendanceList = new ArrayList<>();
            logger.info("Start reading row from mdb db");
            for (Row row : table) {
                Long mdbAttendanceLogId = Long.parseLong(row.get("AttendanceLogId").toString());
                if (mdbAttendanceLogId > maxAttendanceLogId) {
                    this.prepareAttendanceList(attendanceList, row);
                }
            }
            logger.info("Reading from mdb is completed");
            logger.info("Saving attendance record");
            attendanceRepository.saveAll(attendanceList);
            message = attendanceList.size() + " Records updated";
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Exception while processing ", e.getMessage());
            message = e.getMessage();
        }

        return message;
    }

    private void prepareAttendanceList(List<Attendance> attendanceList, Row row) {
        Attendance attendance = new Attendance();
        attendance.setAttendanceLogId(Long.parseLong(row.get("AttendanceLogId").toString()));
        String attendanceDateString = row.get("AttendanceDate").toString();
        Date attendanceDate = DateUtil.getDate(attendanceDateString);
        attendance.setAttendanceDate(new java.sql.Date(attendanceDate.getTime()));
        attendance.setEmployeeId(row.get("EmployeeId").toString());

        Double present = Double.parseDouble(row.get("Present").toString());
        attendance.setPresent(present == 1 ? true : false);

        attendanceList.add(attendance);
    }
}
