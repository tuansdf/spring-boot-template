package com.example.sbt.module.email;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "email")
public class Email extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "to_email", columnDefinition = "text")
    private String toEmail;
    @Column(name = "cc_email", columnDefinition = "text")
    private String ccEmail;
    @Column(name = "subject", columnDefinition = "text")
    private String subject;
    @Column(name = "body", columnDefinition = "text")
    private String body;
    @Column(name = "retry_count")
    private Integer retryCount;
    @Column(name = "type")
    private Integer type;
    @Column(name = "status")
    private String status;
    @Column(name = "is_html")
    private Boolean isHtml;

}
