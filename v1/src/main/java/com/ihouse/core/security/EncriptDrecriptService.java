package com.ihouse.core.security;

public class EncriptDrecriptService {

    public static void main(String[] args) throws Exception {
        SecurityProvider abstractSecurity = new SecurityProvider();

        var st = abstractSecurity.encrypt("Esign#4223");
        System.out.println("Security encrypt is " + st);
        System.out.println("Security deccrypt is " + abstractSecurity.decrypt("0yVSwJ3VlkDzS/efJuku3sd+pTVNmTV8"));
    }
}
