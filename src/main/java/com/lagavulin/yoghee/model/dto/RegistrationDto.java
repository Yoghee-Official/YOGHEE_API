package com.lagavulin.yoghee.model.dto;

import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {
    private String userId;
    private String name;
    private String phoneNo;
    private String email;
    private String password;

    public String validate(){
        if (!validateUserId()) {
            return "아이디는 최소 2자 이상이어야 합니다.";
        }

        if (!validateName()) {
            return "이름은 최소 2자 이상이어야 합니다.";
        }
        // 숫자만 10~11자리
        if (!validatePhoneNo()) {
            return "전화번호 형식이 올바르지 않습니다. (예: 01012345678)";
        }

        // 이메일 정규식
        if (!validateEmail()) {
            return "이메일 형식이 올바르지 않습니다.";
        }

        // 비밀번호: 8자 이상, 영문/숫자/특수문자 각각 최소 1개 포함
        if (!validatePassword()) {
            return "비밀번호는 8자 이상 15자 이하이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.";
        }

        return null;
    }

    public boolean validateUserId(){
        return userId != null && userId.trim().length() >= 2;
    }

    public boolean validateName(){
        return name != null && name.trim().length() >= 2;
    }

    public boolean validateEmail(){
        return email != null && Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }

    public boolean validatePhoneNo(){
        return phoneNo != null && Pattern.matches("^\\d{10,11}$", phoneNo);
    }

    public boolean validatePassword(){
        return password != null && Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,15}$", password);
    }
}
