package com.ebaotech.datainjection.dev.holiday.service;

import org.springframework.web.multipart.MultipartFile;

public interface HolidayService {

    String uploadHolidaySheet(MultipartFile file);
}
