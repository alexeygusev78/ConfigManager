package ru.ag78.api.utils;

/**
 * Password generator util-class
 * @author Alexey Gusev
 */
public class PasswordGenerator {

    /**
     * Generate password by length and template.
     * template: 0 - digits, A - Capital letters, a - small letters, $ - special symbols, U-uniqie symbols
     * @param template
     * @return
     */
    public static String generate(int length, String template) {

        String alphabet = getAlphabet(template);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = getNextChar(alphabet);
            sb.append(c);
        }

        return sb.toString();
    }

    private static char getNextChar(String alphabet) {

        int pos = (int) (Math.random() * (double) alphabet.length());
        return alphabet.charAt(pos);
    }

    private static String getAlphabet(String template) {

        StringBuilder sb = new StringBuilder();

        if (template.contains("0")) {
            sb.append("0123456789");
        }

        if (template.contains("A")) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }

        if (template.contains("a")) {
            sb.append("abcdefghijklmnopqrstuvwxyz");
        }

        if (template.contains("$")) {
            sb.append("!@#$%^&*(\")_+-=?;");
        }

        return sb.toString();
    }
}
