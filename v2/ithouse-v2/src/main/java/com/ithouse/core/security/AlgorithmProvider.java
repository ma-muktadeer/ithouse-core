package com.ithouse.core.security;

import com.ithouse.core.security.interfaces.Algorithm;

import java.util.HashMap;
import java.util.Map;

class AlgorithmProvider implements Algorithm {

    public Map<String, String> arg0() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("A", "MA");
        map.put("B", "NB");
        map.put("C", "OC");
        map.put("D", "PD");
        map.put("E", "QE");
        map.put("F", "RF");
        map.put("G", "SG");
        map.put("H", "TH");
        map.put("I", "UI");
        map.put("J", "VJ");
        map.put("K", "WK");
        map.put("L", "AL");
        map.put("M", "BM");
        map.put("N", "CN");
        map.put("O", "DO");
        map.put("P", "EP");
        map.put("Q", "FQ");
        map.put("R", "GR");
        map.put("S", "HS");
        map.put("T", "IT");
        map.put("U", "JU");
        map.put("V", "KV");
        map.put("W", "!W");
        map.put("X", "@X");
        map.put("Y", "#Y");
        map.put("Z", "$Z");
        map.put("a", "%a");
        map.put("b", "^b");
        map.put("c", "&c");
        map.put("d", "*d");
        map.put("e", "(e");
        map.put("f", ")f");
        map.put("g", "?g");
        map.put("h", "<h");
        map.put("i", ">i");
        map.put("j", "+j");
        map.put("k", "-k");
        map.put("l", "Wl");
        map.put("m", "Xm");
        map.put("n", "Yn");
        map.put("o", "Zo");
        map.put("p", "ap");
        map.put("q", "bq");
        map.put("r", "cr");
        map.put("s", "ds");
        map.put("t", "et");
        map.put("u", "fu");
        map.put("v", "gv");
        map.put("w", "hw");
        map.put("x", "ix");
        map.put("y", "jy");
        map.put("z", "kz");
        map.put("0", "A0");
        map.put("1", "B1");
        map.put("2", "C2");
        map.put("3", "D3");
        map.put("4", "E4");
        map.put("5", "25");
        map.put("6", "K6");
        map.put("7", "L7");
        map.put("8", "M8");
        map.put("9", "N9");
        map.put("!", "O!");
        map.put("@", "P@");
        map.put("#", "Q#");
        map.put("$", "R$");
        map.put("%", "S%");
        map.put("^", "T^");
        map.put("&", "1&");
        map.put("*", "G*");
        map.put("(", "H(");
        map.put(")", "I)");
        map.put("?", "J?");
        map.put("<", "K<");
        map.put(">", "!>");
        map.put("+", "@+");
        map.put("-", "#-");
        map.put(".", "@.");
        map.put("_", "@_");
        return map;
    }

    public Map<String, String> arg1() throws Exception {
        Map<String, String> arg1 = new HashMap<>();

        for (Map.Entry<String, String> stringStringEntry : this.arg0().entrySet()) {
            arg1.put((String) stringStringEntry.getValue(), (String) stringStringEntry.getKey());
        }

        return arg1;
    }
}
