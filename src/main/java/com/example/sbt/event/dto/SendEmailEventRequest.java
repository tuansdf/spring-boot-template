package com.example.sbt.event.dto;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.email.dto.EmailDTO;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendEmailEventRequest implements Serializable {

    private RequestContext requestContext;
    private EmailDTO email;

}
