package com.example.sbt.module.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileObjectDTO {

    private UUID id;
    private String fileUrl;
    private Instant createdAt;
    private Instant updatedAt;

}
