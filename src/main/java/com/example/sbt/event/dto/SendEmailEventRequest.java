package com.example.sbt.event.dto;

import com.example.sbt.core.dto.RequestContextData;
import com.example.sbt.module.email.dto.EmailDTO;
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

    private RequestContextData requestContextData;
    private EmailDTO email;

}
