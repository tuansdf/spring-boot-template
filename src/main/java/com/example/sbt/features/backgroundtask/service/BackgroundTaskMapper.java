package com.example.sbt.features.backgroundtask.service;

import com.example.sbt.features.backgroundtask.dto.BackgroundTaskDTO;
import com.example.sbt.features.backgroundtask.entity.BackgroundTask;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BackgroundTaskMapper {
    BackgroundTaskDTO toDTO(BackgroundTask v);

    BackgroundTask toEntity(BackgroundTaskDTO v);

    BackgroundTask clone(BackgroundTask v);
}
