package com.ithouse.core.security;

import java.util.Map;

public class SecurityUtils {
    public static String[] split(String arg0, int arg1) throws Exception {
        String[] arg2 = new String[arg0.length() / arg1];
        int arg3 = 0;

        for(int i = 0; i < arg0.length(); i += arg1) {
            arg2[arg3] = arg0.substring(i, i + arg1);
            ++arg3;
        }

        return arg2;
    }

    public static String encrypt(String arg0, Map<String, String> arg1) throws Exception {
        StringBuilder arg3 = new StringBuilder();
        String[] arg4 = split(arg0, 1);

        for (String s : arg4) {
            arg3.append((String) arg1.get(s));
        }

        return arg3.toString();
    }

    public static String decrypt(String arg0, Map<String, String> arg1) throws Exception {
        StringBuilder arg3 = new StringBuilder();
        String[] arg4 = split(arg0, 2);

        for (String s : arg4) {
            arg3.append((String) arg1.get(s));
        }

        return arg3.toString();
    }
}
