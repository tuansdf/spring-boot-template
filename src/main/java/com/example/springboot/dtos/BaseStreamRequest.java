package com.example.springboot.dtos;

import lombok.*;
import org.apache.tomcat.util.http.fileupload.RequestContext;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BaseStreamRequest implements Serializable {

    RequestContext requestContext;

}
