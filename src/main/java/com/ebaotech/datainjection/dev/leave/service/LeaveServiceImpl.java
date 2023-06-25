package com.ebaotech.datainjection.dev.leave.service;

import com.ebaotech.datainjection.dev.leave.model.Leave;
import com.ebaotech.datainjection.dev.leave.repository.LeaveRepository;
import com.ebaotech.datainjection.dev.util.ExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Autowired
    private LeaveRepository leaveRepository;

    private String step = null;

    public String uploadLeaveSheet(MultipartFile file) {

        step = "Uploding {} started";
        logger.info(step, file.getOriginalFilename());
        try {
            step = "Create workbook";
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            step = "Get sheet at 0";
            Sheet sheet = workbook.getSheetAt(0);

            List<Leave> leaveList = new ArrayList<>();

            for (int rowNum = 3; rowNum <= sheet.getLastRowNum(); rowNum++) {
                step = "reading row " + rowNum;
                Row row = sheet.getRow(rowNum);
                if (ExcelUtil.isBlankRow(row)) {
                    continue;
                }

                Iterator<Cell> cellIterator = row.cellIterator();

                Leave leave = new Leave();
                for (int col = 1; col < row.getLastCellNum(); col++) {
                    step = "reading col " + col + " of row " + row;
                    Cell cell = row.getCell(col);
                    switch (col) {
                        case 1:
                            leave.setEmpId(cell.getStringCellValue());
                            break;
                        case 2:
                            leave.setFromDate(new java.sql.Date(cell.getDateCellValue().getTime()));
                            break;
                        case 3:
                            leave.setToDate(new java.sql.Date(cell.getDateCellValue().getTime()));
                            break;
                        default:
                    }
                }
                leaveList.add(leave);
            }

            logger.info("Saving leave list to db");
            leaveRepository.saveAll(leaveList);

            logger.info("Uploding {} completed", file.getOriginalFilename());
            logger.info("{} record uploaded", leaveList.size());
        } catch (Exception exce) {
            logger.error(step + "failed");
            logger.error(exce.getMessage());
            return "Data uploading Failed";
        } finally {
            try {
                if (file != null && file.getInputStream() != null)
                    file.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Data uploaded Successfully";
    }
}
