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
public class ExecuteSendEmailStreamRequest implements Serializable {

    RequestContext requestContext;
    UUID emailId;

}
