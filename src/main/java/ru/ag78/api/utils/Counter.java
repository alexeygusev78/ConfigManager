package ru.ag78.api.utils;

/**
 * Counting class.
 * @author Alexey Gusev
 *
 */
public class Counter {

    private int count = 0;
    private long startTime = 0L;

    /**
     * Default ctor
     */
    public Counter() {

        reset();
    }

    /**
     * Сбросить состояние счетчика
     */
    public synchronized void reset() {

        count = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * Увеличивает значение счетчика
     * @return
     */
    public synchronized int increment() {

        return ++count;
    }

    /**
     * Возвращает значение счетчика
     * @return
     */
    public synchronized int get() {

        return count;
    }

    /**
     * Возвращает время замера
     * @return
     */
    public long duration() {

        return System.currentTimeMillis() - startTime;
    }
}
