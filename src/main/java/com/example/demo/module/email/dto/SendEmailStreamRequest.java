package com.example.demo.module.email.dto;

import com.example.demo.dto.RequestContext;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendEmailStreamRequest implements Serializable {

    private RequestContext requestContext;
    private UUID emailId;

}
