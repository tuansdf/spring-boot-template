package com.example.sbt.module.backgroundtask.service;

import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.module.backgroundtask.constant.BackgroundTaskStatus;
import com.example.sbt.module.backgroundtask.dto.BackgroundTaskDTO;
import com.example.sbt.module.backgroundtask.entity.BackgroundTask;
import com.example.sbt.module.backgroundtask.repository.BackgroundTaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class BackgroundTaskServiceImpl implements BackgroundTaskService {
    private final BackgroundTaskRepository backgroundTaskRepository;
    private final BackgroundTaskMapper backgroundTaskMapper;

    @Override
    public BackgroundTaskDTO init(String type) {
        BackgroundTask result = new BackgroundTask();
        result.setType(type);
        result.setStatus(BackgroundTaskStatus.ENQUEUED);
        result.setCreatedBy(RequestContext.get().getUserId());
        return backgroundTaskMapper.toDTO(backgroundTaskRepository.save(result));
    }

    @Override
    public BackgroundTaskDTO findOneById(UUID id) {
        if (id == null) return null;
        return backgroundTaskRepository.findById(id).map(backgroundTaskMapper::toDTO).orElse(null);
    }

    @Override
    public BackgroundTaskDTO findOneByIdOrThrow(UUID id) {
        BackgroundTaskDTO result = findOneById(id);
        if (result == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Override
    public void updateStatus(UUID id, String status, UUID fileId) {
        if (id == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        backgroundTaskRepository.updateStatusById(id, status, fileId);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Override
    public void updateStatus(UUID id, String status) {
        if (id == null) {
            throw new CustomException(HttpStatus.NOT_FOUND);
        }
        backgroundTaskRepository.updateStatusById(id, status);
    }
}
