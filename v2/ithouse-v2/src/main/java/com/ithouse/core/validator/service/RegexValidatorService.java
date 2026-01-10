package com.ithouse.core.validator.service;

import com.ithouse.core.validator.anotations.RegexMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

@Service
public class RegexValidatorService implements ConstraintValidator<RegexMatch, String> {

    private String regexType;

    private static RegexMap regexMap;

    public RegexValidatorService() {
    }

    public RegexValidatorService(String regexType) {
        this.regexType = regexType;
    }

    public void setRegexMap(RegexMap regexMap) {
        RegexValidatorService.regexMap = regexMap;
    }

    public RegexMap getRegexMap() {
        return regexMap;
    }

    @Override
    public void initialize(RegexMatch annotation) {

        this.regexType = annotation.regexType();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return isValid(value);
    }

    public boolean isValid(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        if (regexType == null || regexType.isEmpty()) {
            return true;
        }
        if (regexMap == null) {
            return true;
        } else {
            String ptn = regexMap.getRegex(regexType);
            if (ptn == null) {
                return true;
            }
            return value.matches(regexMap.getRegex(regexType));
        }
    }

}
