package com.example.sbt.module.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileObjectService {

    FileObjectDTO upload(MultipartFile file, String filePath);

    FileObjectDTO uploadImage(MultipartFile file, String dirPath, Integer thumbnailWidth);

    FileObjectDTO uploadImage(MultipartFile file, String dirPath);

    FileObjectDTO uploadImage(MultipartFile file);

}
