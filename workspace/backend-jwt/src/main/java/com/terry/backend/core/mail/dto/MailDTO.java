package com.terry.backend.core.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class MailDTO {

    private String fromMail;
    private String fromName;
    private String toMail;
    private String toName;
    private String subject;
    private String content;
}
