package com.example.sbt.module.backgroundtask.service;

import com.example.sbt.module.backgroundtask.dto.BackgroundTaskDTO;
import com.example.sbt.module.backgroundtask.entity.BackgroundTask;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BackgroundTaskMapper {
    BackgroundTaskDTO toDTO(BackgroundTask v);

    BackgroundTask toEntity(BackgroundTaskDTO v);

    BackgroundTask clone(BackgroundTask v);
}
