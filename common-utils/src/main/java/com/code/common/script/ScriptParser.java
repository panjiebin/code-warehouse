package com.code.common.script;

import com.code.common.utils.ClasspathPackageScanner;

import java.util.*;

/**
 * @author Pan Jiebin
 * @date 2021-02-23 15:18
 */
public class ScriptParser {

    private final List<ScriptHandler> handlers;

    public ScriptParser() {
        this.handlers = lookupHandlers();
    }

    private List<ScriptHandler> lookupHandlers() {
        List<ScriptHandler> handlers = new ArrayList<>();
        String pkgPath = this.getClass().getPackage().getName();
        Set<Class<?>> allClass = ClasspathPackageScanner.scan(pkgPath, this.getClass().getClassLoader());
        allClass.stream()
                .filter(ScriptHandler.class::isAssignableFrom)
                .filter(aClass -> aClass.getAnnotation(Order.class) != null)
                .sorted(Comparator.comparingInt(c -> c.getAnnotation(Order.class).value()))
                .forEach(aClass -> {
                    try {
                        Object handler = aClass.newInstance();
                        handlers.add((ScriptHandler) handler);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                });
        return handlers;
    }

    public String parse(String script, Map<String, Object> params) {
        for (ScriptHandler handler : handlers) {
            script = handler.handle(script, params);
        }
        return script;
    }
}
