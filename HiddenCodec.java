package com.hiddenkeyboard.app;

import java.nio.charset.StandardCharsets;

public final class HiddenCodec {
    private static final char ZERO = '\u200B'; // 0
    private static final char ONE = '\u200C';  // 1
    private static final String PREFIX = "\u2060\u2060\u2060";
    private HiddenCodec() {}

    public static String encode(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        StringBuilder out = new StringBuilder(PREFIX.length() + bytes.length * 8);
        out.append(PREFIX);
        for (byte b : bytes) {
            int v = b & 0xFF;
            for (int i = 7; i >= 0; i--) out.append(((v >> i) & 1) == 1 ? ONE : ZERO);
        }
        return out.toString();
    }

    public static String decode(String input) {
        int p = input.indexOf(PREFIX);
        if (p < 0) return null;
        String body = input.substring(p + PREFIX.length());
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == ZERO) bits.append('0');
            else if (c == ONE) bits.append('1');
        }
        if (bits.length() == 0 || bits.length() % 8 != 0) return null;
        byte[] bytes = new byte[bits.length() / 8];
        for (int i = 0; i < bytes.length; i++) {
            int v = 0;
            for (int j = 0; j < 8; j++) v = (v << 1) | (bits.charAt(i * 8 + j) - '0');
            bytes[i] = (byte) v;
        }
        try { return new String(bytes, StandardCharsets.UTF_8); }
        catch (Exception e) { return null; }
    }

    public static boolean looksEncoded(String input) { return input != null && input.contains(PREFIX); }
}
