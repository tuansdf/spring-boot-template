package com.example.sbt.module.user.event;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.user.dto.SearchUserRequest;
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
    private RequestContext requestContext;
    private SearchUserRequest searchRequest;
    private UUID backgroundTaskId;
}
