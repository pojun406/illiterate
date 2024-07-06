package com.illiterate.illiterate.event.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailCertificateRequestDto(
        @NotBlank
        @Email
        String email
) {
}