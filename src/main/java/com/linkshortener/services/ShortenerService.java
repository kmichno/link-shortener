package com.linkshortener.services;

public class ShortenerService {

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final int Base = ALPHABET.length();

    public static String encode(int i)
    {
        if (i == 0) return String.valueOf(ALPHABET.charAt(0));

        String s = "";

        while (i > 0)
        {
            s = s.concat(String.valueOf(ALPHABET.charAt(i % Base)));
            i = i / Base;
        }

        StringBuilder sb = new StringBuilder(s);
        return sb.reverse().toString();
    }

    public static int decode(String s)
    {
        int i = 0;

        for (char symbol : s.toCharArray())
        {
            i = (i * Base) + ALPHABET.indexOf(symbol);
        }

        return i;
    }
}
