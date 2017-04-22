package ru.ag78.api.utils;

import java.security.MessageDigest;

/**
 * Unique identier generator.
 * @author Alexey Gusev
 *
 */
public class Generator {

    /**
     * Счетчик попыток генерации
     */
    private static long count;

    /**
     * Статический блок инициализации
     */
    static {
        count = 1;
    }

    public static synchronized byte[] getNextMD5Raw(String source) throws Exception {

        long index = incrementCount();

        String src = String.format("%s%d%d", SafeTypes.getSafeString(source), System.currentTimeMillis(), index);
        return MessageDigest.getInstance("MD5").digest(src.getBytes("UTF-16LE"));
    }

    public static synchronized byte[] getNextMD5RawSafe(String source) {

        try {
            return getNextMD5Raw(source);
        } catch (Exception e) {
        }

        return getNextMD5Safe(source).getBytes();
    }

    /**
     * Генерировать следующий идентификатор в формате MD5
     * @return
     */
    public static synchronized String getNextMD5() throws Exception {

        return getNextMD5("");
    }

    /**
     * Генерировать следующий идентификатор в формате MD5 на основании предоставленных исходных данных + случайные данные.
     * @param source
     * @return
     * @throws Exception
     */
    public static synchronized String getNextMD5(String source) throws Exception {

        long index = incrementCount();

        String src = String.format("%s%d%d", SafeTypes.getSafeString(source), System.currentTimeMillis(), index);
        return StringUtils.bytesToString(MessageDigest.getInstance("MD5").digest(src.getBytes("UTF-16LE")));
    }

    /**
     * Безопасно сгенерировать следующий идентификатор в формате MD5
     * @return
     */
    public static synchronized String getNextMD5Safe() {

        return getNextMD5Safe("");
    }

    /**
     * Безопасно сгенерировать следующий идентификатор в формате MD5 на основании предоставленных данных
     * @return
     */
    public static synchronized String getNextMD5Safe(String source) {

        try {
            return getNextMD5(source);
        } catch (Exception e) {
            return String.format("%x%x", System.currentTimeMillis(), incrementCount());
        }
    }

    /**
     * Сгенерировать код MD5 для заданной строки.
     * @param src
     * @return
     * @throws Exception
     */
    public static String makeMD5ForString(String src) throws Exception {

        return StringUtils.bytesToString(MessageDigest.getInstance("MD5").digest(src.getBytes("UTF-8")));
    }

    /**
     * Безопасно сгенерировать код MD5 для заданной строки.
     * @param src
     * @return
     */
    public static String makeMD5ForStringSafe(String src) {

        try {
            return makeMD5ForString(src);
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * Увеличить счетчик попыток генерации хеша и вернуть его значение 
     * @return
     */
    public static synchronized long incrementCount() {

        return count++;
    }
}
