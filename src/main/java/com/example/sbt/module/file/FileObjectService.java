package com.example.sbt.module.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileObjectService {

    FileObjectDTO upload(MultipartFile file, String filePath) throws IOException;

    FileObjectDTO uploadImage(MultipartFile file, String dirPath, Integer thumbnailWidth) throws IOException;

    FileObjectDTO uploadImage(MultipartFile file, String dirPath) throws IOException;

    FileObjectDTO uploadImage(MultipartFile file) throws IOException;

}
