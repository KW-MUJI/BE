package com.muji_backend.kw_muji.user.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class ValidCheck {
    public static boolean isValidMail(String email) {
        String regexMail = "^.*@kw.ac.kr$";

        if(email == null || email.isBlank()) {
            log.warn("이메일이 공백임");
            return false;
        } else if(!Pattern.matches(regexMail, email)) {
            log.warn("이메일이 규칙에 맞지 않음");
            return false;
        } else {
            return true;
        }
    }
}
