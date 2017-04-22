package ru.ag78.api.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Набор безопасных функций для работы с массивами.
 * @author Alexey Gusev
 *
 */
public class SafeArrays {

    /**
     * Безопасно вернуть элемент массива, приведенный к строке с указанием значения по-умолчанию.
     * @param array
     * @return
     */
    public static String getSafeItem(List<?> array, int index, String defaultValue) {

        try {
            return SafeTypes.getSafeString(array.get(index));
        } catch (Exception e) {
        }

        return defaultValue;
    }

    /**
     * Безопасно вернуть элемент массива, приведенный к строке.<br/>
     * В случае проблем будет возвращена пустая строка.
     * @param array
     * @param index
     * @return
     */
    public static String getSafeItem(List<?> array, int index) {

        return getSafeItem(array, index, "");
    }

    /**
     * Безопасно вернуть элемент строкового массива. В случае ошибки будет возвращено значение по-умолчанию.
     * @param array
     * @param index
     * @param defaultValue
     * @return
     */
    public static String getSafeItem(String[] array, int index, String defaultValue) {

        try {
            return SafeTypes.getSafeString(array[index]);
        } catch (Exception e) {
        }

        return defaultValue;
    }

    /**
     * Безопасно вернуть элемент строкового массива.<br/>
     * В случае ошибки будет возвращена пустая строка.
     * @param array
     * @param index
     * @return
     */
    public static String getSafeItem(String[] array, int index) {

        return getSafeItem(array, index, "");
    }

    /**
     * Parses to map-in-line the following config-string:
     * key1:value1;key2:value2;key3:value3;key4:value4....
     * @param mapInLine
     * @return
     */
    public static Map<String, String> parseStringToMap(String mapInLine) {

        Map<String, String> mymap = new HashMap<String, String>();
        if (mapInLine == null) {
            return mymap;
        }

        String[] groups = mapInLine.split(";");
        for (String group: groups) {
            if (group.trim().length() > 0) {
                mymap.put(KeyValue.getKey(group), SafeTypes.getSafeString(KeyValue.getValue(group)));
            }
        }

        return mymap;
    }

    /**
     * Parses map to string format:<br/>
     * key1=value1<br/>
     * key2=value2<br/>
     * ...
     * @param mapToPrint
     * @return string representation of the map
     */
    public static <V, K> String parseMapToString(Map<K, V> mapToPrint) {

        StringBuilder sb = new StringBuilder();
        for (K key: mapToPrint.keySet()) {
            String value = mapToPrint.get(key) == null ? "null" : mapToPrint.get(key).toString();
            sb.append(key.toString()).append("=").append(value).append("\n");
        }

        return sb.toString();
    }
}
