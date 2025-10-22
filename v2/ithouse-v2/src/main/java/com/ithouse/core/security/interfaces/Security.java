package com.ithouse.core.security.interfaces;

public interface Security {
    String encrypt(String var1) throws Exception;

    String decrypt(String var1) throws Exception;

    String get(String var1) throws Exception;
}
