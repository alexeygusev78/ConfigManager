package ru.ag78.api.utils;

/**
 * Key-Value pair.<br/>
 * @author Alexey Gusev
 *
 */
public class KeyValue {

    private String key;
    private String value;

    /**
     * Конструктор с инициализацией из выражения key:value
     * @param expr
     */
    public KeyValue(String expr) {

        this.key = KeyValue.getKey(expr);
        this.value = KeyValue.getValue(expr);
    }

    /**
     * Конструктор с инициализацией из отдельных параметров key, value
     * @param key
     * @param value
     */
    public KeyValue(String key, String value) {

        this.key = SafeTypes.getSafeString(key);
        this.value = SafeTypes.getSafeString(value);
    }

    /**
     * Вернуть значение key для объекта
     * @return
     */
    public String getKey() {

        return SafeTypes.getSafeString(key);
    }

    /**
     * Вернуть значение value для объекта
     * @return
     */
    public String getValue() {

        return SafeTypes.getSafeString(value);
    }

    /**
     * Перегрузить оператор toString. 
     * Выводим в формате 'key:value', если key задано, 
     * в противном случае выводим в формате 'value'.
     * Если key задано, а value не задано, то выводим в формате 'key:'.
     */
    @Override
    public String toString() {

        if (getKey().length() == 0) {
            return getValue();
        }

        return getKey() + ":" + getValue();
    }

    /**
     * Вернуть ключ из выражения key:value.<br/>
     * Если разделителя нет, то будет возвращена пустая строка.
     * @param expr
     * @return
     */
    public static String getKey(String expr) {

        expr = SafeTypes.getSafeString(expr);
        String[] tokens = expr.split(":");

        if (tokens.length == 0) {
            return "";
        }

        if (tokens.length == 1) {
            int pos = expr.indexOf(':');
            if (pos <= 0) {
                // разделитель стоит спереди или его нет вообще
                return "";
            } else {
                return SafeTypes.getSafeString(tokens[0]);
            }
        }

        return SafeTypes.getSafeString(tokens[0]);
    }

    /**
     * Вернуть значение из выражения key:value.<br/>
     * Если разделителя нет, то считается, что expr и есть значение value.<br/>
     * Если разделитель найден, то будет возвращено значение с индексом 1.
     * @param expr
     * @return
     */
    public static String getValue(String expr) {

        expr = SafeTypes.getSafeString(expr);
        String[] tokens = expr.split(":");
        if (tokens.length == 0) {
            return "";
        } else if (tokens.length == 1) {
            int pos = expr.indexOf(':');
            if (pos <= 0) {
                // разделителя нет вообще или стоит спереди
                return tokens[0];
            } else {
                // разделитель стоит сзади
                return "";
            }
        }

        return SafeTypes.getSafeString(tokens[1]);
    }
}
