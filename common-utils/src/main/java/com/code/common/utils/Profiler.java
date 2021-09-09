package com.code.common.utils;

/**
 * @author Pan Jiebin
 * @date 2020-09-14 23:31
 */
public class Profiler {

    private final static ThreadLocal<Long> TIME = ThreadLocal.withInitial(System::currentTimeMillis);

    private Profiler() {
    }

    public static long begin() {
        clear();
        long current = System.currentTimeMillis();
        TIME.set(current);
        return current;
    }
    
    public static long end() {
        return System.currentTimeMillis() - TIME.get();
    }

    public static long end(long start) {
        return System.currentTimeMillis() - start;
    }

    public static void clear() {
        TIME.remove();
    }

}
