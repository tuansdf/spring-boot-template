package com.example.springboot.modules.email.dtos;

import com.example.springboot.dtos.RequestContext;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendEmailStreamRequest implements Serializable {

    RequestContext requestContext;
    UUID emailId;

}
