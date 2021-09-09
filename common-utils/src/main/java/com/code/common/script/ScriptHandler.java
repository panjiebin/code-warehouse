package com.code.common.script;

import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2021-02-23 14:59
 */
interface ScriptHandler {

    /**
     * process script
     * @param script script
     * @param params params
     * @return script
     */
    String handle(String script, Map<String, Object> params);
}
