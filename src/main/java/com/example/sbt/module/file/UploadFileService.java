package com.example.sbt.module.file;

import java.net.URL;

public interface UploadFileService {

    boolean upload(String filePath, byte[] file);

    URL getFileUrl(String filePath);

}
