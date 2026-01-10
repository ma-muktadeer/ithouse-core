package com.ithouse.core.validator.service;

import java.util.Map;

public class RegexMap {
    public Map<String, String> regexValidatorPattern;

    public RegexMap() {
    }

    public Map<String, String> geRegexMap() {
        return this.regexValidatorPattern;
    }

    public void setRegexMap(Map<String, String> regexMap) {
        this.regexValidatorPattern = regexMap;
    }

    protected String getRegex(String regexType) {
        return regexValidatorPattern.get(regexType);
    }

}
