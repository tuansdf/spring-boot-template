package com.example.sbt.module.file;

import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileObjectServiceImpl implements FileObjectService {

    private final CommonMapper commonMapper;
    private final UploadFileService uploadFileService;
    private final FileObjectRepository fileObjectRepository;

    @Override
    public FileObjectDTO upload(String filePath, byte[] file) {
        String url = uploadFileService.upload(filePath, file);
        if (url == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObject result = new FileObject();
        result.setFileUrl(url);
        result = fileObjectRepository.save(result);
        return commonMapper.toDTO(result);
    }

}
