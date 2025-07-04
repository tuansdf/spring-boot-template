package com.example.sbt.event.dto;

import com.example.sbt.core.dto.RequestContextData;
import com.example.sbt.module.user.dto.SearchUserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportUserEventRequest implements Serializable {
    private RequestContextData requestContext;
    private SearchUserRequestDTO searchRequest;
    private UUID backgroundTaskId;
}
