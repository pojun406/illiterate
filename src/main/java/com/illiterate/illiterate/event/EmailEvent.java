package com.illiterate.illiterate.event;

import org.springframework.context.ApplicationEvent;

public class EmailEvent extends ApplicationEvent {
    private final String email;
    private final String verificationCode;

    public EmailEvent(Object source, String email, String verificationCode) {
        super(source);
        this.email = email;
        this.verificationCode = verificationCode;
    }

    public String getEmail() {
        return email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
