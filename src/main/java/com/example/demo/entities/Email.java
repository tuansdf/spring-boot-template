package com.example.demo.entities;

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
public class Email extends AbstractEntity {

    @Column(name = "from_email", columnDefinition = "text")
    private String fromEmail;
    @Column(name = "to_email", columnDefinition = "text")
    private String toEmail;
    @Column(name = "cc_email", columnDefinition = "text")
    private String ccEmail;
    @Column(name = "subject", columnDefinition = "text")
    private String subject;
    @Column(name = "content", columnDefinition = "text")
    private String content;
    @Column(name = "type")
    private Integer type;
    @Column(name = "retry_count")
    private Integer retryCount;
    @Column(name = "status")
    private Integer status;
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
    @Column(name = "updated_by")
    private UUID updatedBy;

}
