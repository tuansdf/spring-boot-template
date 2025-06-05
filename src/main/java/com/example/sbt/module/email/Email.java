package com.example.sbt.module.email;

import com.example.sbt.common.entity.BaseEntity;
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
                @Index(name = "email_created_at_idx", columnList = "created_at"),
        }
)
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
    private String type;
    @Column(name = "status")
    private String status;
    @Column(name = "is_html")
    private Boolean isHtml;

}
