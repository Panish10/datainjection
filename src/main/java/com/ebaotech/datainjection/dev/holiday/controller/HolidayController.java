package com.ebaotech.datainjection.dev.holiday.controller;

import com.ebaotech.datainjection.dev.holiday.model.Holiday;
import com.ebaotech.datainjection.dev.holiday.repository.HolidayRepository;
import com.ebaotech.datainjection.dev.holiday.service.HolidayService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/holidays")
public class HolidayController {

    private static final Logger logger = LoggerFactory.getLogger(HolidayController.class);

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private HolidayService holidayService;

    @PostMapping("/upload")
    @ApiOperation(value = "Upload Holiday", notes = "Upload excel file of Holiday list")
    public String uploadHolidaySheet(@RequestParam("file") MultipartFile file) {
        return holidayService.uploadHolidaySheet(file);
    }

    @GetMapping
    @ApiOperation(value = "Get Holiday List", notes = "Get all holidays without criteria")
    public List<Holiday> getAllHolidays() {
        logger.info("Retrieving all Holidays");
        return holidayRepository.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Find Holiday by id", notes = "Provide an id to look up specific item from Holiday list")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable Long id) {
        logger.info("Retrieving Holiday by ID: {}", id);
        return holidayRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/year/{year}")
    @ApiOperation(value = "Get Holiday list by year", notes = "Get Holiday list by year")
    public List<Holiday> getAllHolidaysByYear(@PathVariable @DateTimeFormat(pattern = "yyyy") Date year) {
        logger.info("Retrieving Holidays by year: {}", year);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date endDate = calendar.getTime();
        return holidayRepository.findByDateBetween(startDate, endDate);
    }

    @PostMapping
    @ApiOperation(value = "Create new Holiday", notes = "Create new Holiday")
    public Holiday createHoliday(@RequestBody Holiday holiday) {
        logger.info("Creating a new Holiday");
        return holidayRepository.save(holiday);
    }

    @PutMapping
    @ApiOperation(value = "Update existing Holiday", notes = "Update existing Holiday")
    public ResponseEntity<Holiday> updateHoliday(@RequestBody Holiday holiday) {
        logger.info("Updating holiday: {}", holiday);
        if (!holidayRepository.existsById(holiday.getId())) {
            return ResponseEntity.notFound().build();
        }

        Holiday updated = holidayRepository.save(holiday);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete existing Holiday by Id", notes = "Delete existing Holiday by Id")
    public ResponseEntity<Void> deteleHolidy(@PathVariable Long id) {
        logger.info("Deleting holiday by ID: {}", id);
        if (!holidayRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        holidayRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
