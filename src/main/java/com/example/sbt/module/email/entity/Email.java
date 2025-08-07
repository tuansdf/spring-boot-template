package com.example.sbt.module.email.entity;

import com.example.sbt.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "email",
        indexes = {
                @Index(name = "email_user_id_idx", columnList = "user_id"),
                @Index(name = "email_to_email_idx", columnList = "to_email"),
                @Index(name = "email_created_at_idx", columnList = "created_at"),
        }
)
public class Email extends BaseEntity {
    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "to_email", columnDefinition = "text")
    private String toEmail;
    @Column(name = "cc_email", columnDefinition = "text")
    private String ccEmail;
    @Column(name = "subject", columnDefinition = "text")
    private String subject;
    @Column(name = "body", columnDefinition = "text")
    private String body;
    @Column(name = "is_html")
    private Boolean isHtml;
    @Column(name = "retry_count")
    private Integer retryCount;
    @Column(name = "type", length = 32)
    private String type;
    @Column(name = "status", length = 16)
    private String status;
    @Column(name = "send_status", length = 16)
    private String sendStatus;
}
