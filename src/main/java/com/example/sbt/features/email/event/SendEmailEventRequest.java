package com.example.sbt.features.email.event;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.features.email.dto.EmailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailEventRequest implements Serializable {
    private RequestContext requestContext;
    private EmailDTO email;
}
