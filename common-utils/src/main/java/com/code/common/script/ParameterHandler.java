package com.code.common.script;

import com.code.common.utils.ParameterParser;

import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2021-02-23 15:29
 */
@Order(Integer.MAX_VALUE)
class ParameterHandler implements ScriptHandler {

    @Override
    public String handle(String script, Map<String, Object> params) {
        if (ParameterParser.parseParameters(script).isEmpty()) {
            return script;
        }
        return ParameterParser.parse(script, params);
    }
}
