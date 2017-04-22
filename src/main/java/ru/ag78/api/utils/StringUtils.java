package ru.ag78.api.utils;

import java.security.MessageDigest;

/**
 * String utls
 * 
 * @author Alexey Gusev
 * 
 */
public class StringUtils {

    /**
     * Создать сторку, сформированную из count символов ch
     * 
     * @param ch
     * @param count
     * @return
     */
    public static String repeatChar(char ch, int count) {

        StringBuffer outputBuffer = new StringBuffer(count);
        for (int i = 0; i < count; i++) {
            outputBuffer.append(ch);
        }

        return outputBuffer.toString();
    }

    /**
     * Собрать строку, содержащую из массива байтов
     * 
     * @param digest
     * @return
     */
    public static String bytesToString(byte[] digest) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(String.format("%2X", digest[i]));
        }
        return sb.toString().replace(' ', '0');
    }

    /**
     * Сформировать хэш по исходной строке в кодировке UTF-8
     * 
     * @param source
     * @return
     */
    public static byte[] getHash(String source) throws Exception {

        return MessageDigest.getInstance("MD5").digest(source.getBytes("UTF-8")); // UTF-16LE
    }

    /**
     * Сформировать строку, содержащую хэш, представленную в hex виде
     * 
     * @param source
     * @return
     * @throws Exception
     */
    public static String getHashString(String source) throws Exception {

        return SafeTypes.getSafeString(StringUtils.bytesToString(StringUtils.getHash(source)));
    }

    /**
     * Вывести байтовый буфер в hex-строку.
     * По-умолчанию выводится весь буфер в строку без разделителей.
     * @param buffer
     * @return
     */
    public static String binaryToHexString(byte[] buffer) {

        return binaryToHexString(buffer, 0, "");
    }

    /**
     * Вывести байтовый буфер в hex-строку
     * @param buffer
     * @param size - максимальное кол-во байт, подлежащее выводу. Если size=0, то выводится весь буфер.
     * @param delimeter - разделитель между байтами
     * @return
     */
    public static String binaryToHexString(byte[] buffer, int size, String delimeter) {

        if (size == 0) {
            size = buffer.length;
        } else {
            size = Math.min(buffer.length, size);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(String.format("%02x%s", buffer[i], delimeter));
        }

        return sb.toString();
    }
}
