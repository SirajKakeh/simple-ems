package com.task.ems.service;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {
    void uploadFile(final String userId, MultipartFile multipartFile);
}
