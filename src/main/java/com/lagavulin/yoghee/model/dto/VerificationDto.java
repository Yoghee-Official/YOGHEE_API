package com.lagavulin.yoghee.model.dto;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lagavulin.yoghee.model.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDto {
    private VerificationType type;     // "EMAIL" or "PHONE"
    private String email;
    private String phoneNo;
    private String code;

    @JsonCreator
    public VerificationDto(@JsonProperty("type") String type,
        @JsonProperty("email") String email,
        @JsonProperty("phoneNo") String phoneNo,
        @JsonProperty("code") String code) {
        this.type = VerificationType.fromString(type);
        this.email = email;
        this.phoneNo = phoneNo;
        this.code = code;
    }

    public boolean validate() {
        switch (type) {
            case EMAIL:
                return validateEmail();
            case PHONE:
                return validatePhoneNo();
            default:
                return false;
        }
    }
    private boolean validateEmail(){
        return email != null && Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }

    private boolean validatePhoneNo(){
        return phoneNo != null && Pattern.matches("^\\d{10,11}$", phoneNo);
    }
}
