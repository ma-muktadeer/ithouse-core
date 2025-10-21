package com.ihouse.core.security;

public interface Security {
    String encrypt(String var1) throws Exception;

    String decrypt(String var1) throws Exception;

    String get(String var1) throws Exception;
}

