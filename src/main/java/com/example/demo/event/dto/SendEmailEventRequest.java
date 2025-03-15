package com.example.demo.event.dto;

import com.example.demo.common.dto.RequestContext;
import com.example.demo.module.email.dto.EmailDTO;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendEmailEventRequest implements Serializable {

    private RequestContext requestContext;
    private UUID emailId;
    private EmailDTO email;

}
