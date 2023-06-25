package com.ebaotech.datainjection.testapi.testmodule.controller;

import com.ebaotech.datainjection.testapi.testmodule.model.MDBAttendance;
import com.ebaotech.datainjection.testapi.testmodule.service.MDBMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/testapi")
public class MDBMetadataController {

    @Autowired
    private MDBMetadataService mdbMetadataService;

    @Value("${mdb.download}")
    private String download;

    @GetMapping("/tables")
    public Set<String> getTableNames() {
        return mdbMetadataService.getTableNames();
    }

    @GetMapping("/columns/{tableName}")
    public List<String> getColumnNames(@PathVariable String tableName) {
        return mdbMetadataService.getColumnNames(tableName);
    }

    /*@GetMapping("/createTable")
    public String crateTable() {
        return mdbMetadataService.createTable();
    }*/

    /*@PostMapping("/insertData")
    public MDBAttendance insertData(@RequestBody MDBAttendance dataModel) {
        return mdbMetadataService.insertData(dataModel);
    }*/

    /*@GetMapping("/getAttendanceList")
    public List<MDBAttendance> getAttendanceList() {
        return mdbMetadataService.getAttendanceList();
    }*/

    @GetMapping("/getAllRecord/{dbName}/{tableName}")
    public String getAllRecord(@PathVariable String dbName, @PathVariable String tableName) {
        String message = null;
        if (download.equalsIgnoreCase("true")) {
            message = mdbMetadataService.downloadAllRecord(dbName, tableName);
        } else {
            message = mdbMetadataService.getAllRecord(dbName, tableName);
        }
        return message != null ? message : "Check console";
    }

    @GetMapping("/getIncrementalRecord/{dbName}/{tableName}")
    public String getIncrementalRecord(@PathVariable String dbName, @PathVariable String tableName) {
        String message = null;
        message = mdbMetadataService.getIncrementalRecord(dbName, tableName);
        return message != null ? message : "Check console";
    }
}
