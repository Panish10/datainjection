package com.ebaotech.datainjection.dev.leave.service;

import org.springframework.web.multipart.MultipartFile;

public interface LeaveService {

    String uploadLeaveSheet(MultipartFile file);
}
