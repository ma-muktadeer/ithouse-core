package com.ithouse.core.security;

public class EncryptDecryptService {
    static final SecurityProvider abstractSecurity = new SecurityProvider();

    public static void main(String[] args) throws Exception {

        var st = abstractSecurity.encrypt("Esign#4223");
        System.out.println("Security encrypt is " + st);
        System.out.println("Security deccrypt is " + decrypt(st));
    }

    public static String encrypt(String str) throws Exception {
        return abstractSecurity.encrypt(str);
    }

    public static String decrypt(String str) throws Exception {
        return abstractSecurity.decrypt(str);
    }
}
