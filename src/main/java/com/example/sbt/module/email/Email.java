package com.example.sbt.module.email;

import com.example.sbt.core.entity.BaseEntity;
import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.module.email.dto.EmailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
@SqlResultSetMapping(name = ResultSetName.EMAIL_SEARCH, classes = {
        @ConstructorResult(targetClass = EmailDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "user_id", type = UUID.class),
                @ColumnResult(name = "to_email", type = String.class),
                @ColumnResult(name = "cc_email", type = String.class),
                @ColumnResult(name = "subject", type = String.class),
                @ColumnResult(name = "body", type = String.class),
                @ColumnResult(name = "retry_count", type = Integer.class),
                @ColumnResult(name = "type", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "send_status", type = String.class),
                @ColumnResult(name = "is_html", type = Boolean.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
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
    @Column(name = "retry_count")
    private Integer retryCount;
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private String status;
    @Column(name = "send_status")
    private String sendStatus;
    @Column(name = "is_html")
    private Boolean isHtml;

}
