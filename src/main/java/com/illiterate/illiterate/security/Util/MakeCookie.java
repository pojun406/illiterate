package com.illiterate.illiterate.security.Util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class MakeCookie {

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
