package org.example.back.domain;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailRequest {

    private String to;
    private String subject;
    private String message;
    private String pdfBase64; // PDF encodé en base64
    private String pdfFileName;
}
