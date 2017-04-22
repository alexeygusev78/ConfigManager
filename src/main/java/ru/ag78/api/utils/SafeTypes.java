package ru.ag78.api.utils;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Safety work with all common functions.
 * Static function set.
 * 
 * @author Alexey Gusev
 *
 */
public class SafeTypes {

    /**
     * Generates hex byte values representation of supplied string in current encoding.
     * @param src
     * @return
     */
    public static String getEncodedString(String src) {

        byte[] bytes = src.getBytes();

        String output = "";
        for (int i = 0; i < bytes.length; i++) {
            output += String.format("%2X", bytes[i]);
        }

        return output;
    }

    /**
     * Generates hex byte values representation of supplied string in supplied encoding.
     * @param src
     * @param codePage
     * @return
     */
    public static String getEncodedString(String src, String codePage) {

        try {
            byte[] bytes = src.getBytes(codePage);

            String output = "";
            for (int i = 0; i < bytes.length; i++) {
                output += String.format("%2X", bytes[i]);
            }

            return output;
        } catch (UnsupportedEncodingException e) {
            return SafeTypes.getEncodedString(src);
        }
    }

    /**
     * Safely returns string from Object. 
     * Returns empty string if supplied string is null.
     * @param obj
     * @return
     */
    public static String getSafeString(Object obj) {

        return getSafeString(obj, "");
    }

    /**
     * Safely returns string from Object. 
     * Returns supplied defaultString string if supplied string is null.
     * @param obj
     * @return
     */
    public static String getSafeString(Object obj, String defaultStr) {

        try {
            if (obj == null) {
                return defaultStr;
            }

            return obj.toString();
        } catch (Exception e) {
        }

        return defaultStr;
    }

    /**
     * Safely returns and trim a string; 
     * Returns empty string if error occured.
     * @param str
     * @return
     */
    public static String getDBString(String str) {

        return SafeTypes.getSafeString(str).trim();
    }

    /**
     * Safely returns and trim the string from result set with supplied column name.
     * Returns empty string if error occured.
     * @param rs
     * @param columnName
     * @return
     */
    public static String getDBString(ResultSet rs, String columnName) {

        try {
            return getDBString(rs.getString(columnName));
        } catch (SQLException e) {
        }

        return "";
    }

    /**
     * Safely get java.util.Date from supplied ResultSet and columnName.
     * Returns null if error.
     * An Exception will be never thrown.
     * @param rs
     * @param columnName
     * @return
     */
    public static Date getDBDate(ResultSet rs, String columnName) {

        try {
            return new Date(rs.getDate(columnName).getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Безопасный парсинг целочисленного значения.
     * В случае ошибки возвращается установленное по-умолчанию значение.
     * @param str
     * @param defaultValue
     * @return
     */
    public static int parseSafeInt(String str, int defaultValue) {

        try {
            return Integer.parseInt(getDBString(str));
        } catch (Exception e) {
        }

        return defaultValue;
    }

    /**
     * Безопасный парсинг целочисленного значения long.
     * В случае ошибки возвращается установленное по-умолчанию значение.
     * @param str
     * @param defaultValue
     * @return
     */
    public static long parseSafeLong(String str, long defaultValue) {

        try {
            return Long.parseLong(str.trim());
        } catch (Exception e) {
        }

        return defaultValue;
    }

    /**
     * Безопасный парсинг значения типа double.
     * В случае ошибки возвращается установленное по-умолчанию значение.
     * @param str
     * @param defaultValue
     * @return
     */
    public static double parseSafeDouble(String str, double defaultValue) {

        try {
            return Double.parseDouble(getDBString(str));
        } catch (Exception e) {
        }

        return defaultValue;
    }

    /**
     * Безопасно разобрать Boolean.
     * @param str
     * @param defaultValue
     * @return
     */
    public static boolean parseSafeBoolean(String str, boolean defaultValue) {

        str = SafeTypes.getSafeString(str).toLowerCase().trim();
        if (str.equalsIgnoreCase("true")) {
            return true;
        } else if (str.equalsIgnoreCase("false")) {
            return false;
        }

        return defaultValue;
    }

    /**
     * Разобрать строку в объектный тип Boolean.</br>
     * Возможны 3 варианта: null, true, false.<br/>
     * true: 1, t, true
     * false: 0, f, false
     * null: все остальное
     * @param value
     * @return
     */
    public static Boolean parseBooleanTriState(String value) {

        String strs[] = {"1", "t", "true", "0", "f", "false"};
        Boolean vals[] = {true, true, true, false, false, false};

        value = SafeTypes.getSafeString(value).toLowerCase().trim();
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equalsIgnoreCase(value)) {
                return new Boolean(vals[i]);
            }
        }

        return null;
    }

    /**
     * Безопасный декодинг целочисленного значения long.
     * В случае ошибки возвращается установленное по-умолчанию значение.
     * @param str
     * @param defaultValue
     * @return
     */
    public static long decodeSafeLong(String str, long defaultValue) {

        try {
            return Long.decode(str.trim());
        } catch (Exception e) {
        }

        return defaultValue;
    }

    /**
     * Безопасный кастинг long в int
     * @param l
     * @return
     */
    public static int safeLongToInt(long l) {

        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    /**
     * Возвращает TRUE, только если значение value соответствует указанной маске mask.
     * @param value
     * @param bitMask
     * @return
     */
    public static boolean isBitSet(long value, long bitMask) {

        return ((value & bitMask) == bitMask);
    }

    /**
     * Возвращает TRUE, только если биты из значения value установлены в 0, в соответствии с маской.
     * @param value
     * @param bitMask
     * @return
     */
    public static boolean isBitNotSet(long value, long bitMask) {

        return ((~value & bitMask) == bitMask);
    }

    public static Date parseSafeDate(String source, SimpleDateFormat sdf, Date defaultValue) {

        try {
            return sdf.parse(source);
        } catch (Exception e) {
        }

        return defaultValue;
    }

    /**
     * Безопасно выполняет unboxing для типа boolean.
     * Возвращает значение value.
     * Если в процессе unboxing'а возникнет проблема, то будет возвращено значение defaultValue
     * @param value
     * @param defaultValue
     * @return
     */
    public static boolean unboxingSafe(Boolean value, boolean defaultValue) {

        try {
            return value.booleanValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
