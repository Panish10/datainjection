package com.ebaotech.datainjection.dev.holiday.service;

import com.ebaotech.datainjection.dev.holiday.model.Holiday;
import com.ebaotech.datainjection.dev.holiday.repository.HolidayRepository;
import com.ebaotech.datainjection.dev.util.ExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayServiceImpl.class);

    @Autowired
    private HolidayRepository holidayRepository;

    private String step = null;

    public String uploadHolidaySheet(@RequestParam("file") MultipartFile file) {

        step = "Uploding {} started";
        logger.info(step, file.getOriginalFilename());
        try {
            step = "Create workbook";
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            step = "Get sheet at 0";
            Sheet sheet = workbook.getSheetAt(0);

            List<Holiday> holidayList = new ArrayList<>();
            for (int rowNum = 3; rowNum <= sheet.getLastRowNum(); rowNum++) {
                step = "reading row " + rowNum;
                Row row = sheet.getRow(rowNum);
                if (ExcelUtil.isBlankRow(row)) {
                    continue;
                }
                Holiday holiday = new Holiday();
                for (int col = 1; col < row.getLastCellNum(); col++) {
                    step = "reading col " + col + " of row " + row;
                    Cell cell = row.getCell(col);
                    switch (col) {
                        case 1:
                            holiday.setDate(new java.sql.Date(cell.getDateCellValue().getTime()));
                            break;
                        case 2:
                            holiday.setDay(cell.getStringCellValue());
                            break;
                        case 3:
                            holiday.setDescription(cell.getStringCellValue());
                            break;
                        default:
                    }
                }
                holidayList.add(holiday);
            }
            logger.info("Saving holiday list to db");
            holidayRepository.saveAll(holidayList);

            logger.info("Uploding {} completed", file.getOriginalFilename());
            logger.info("{} record uploaded", holidayList.size());
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
