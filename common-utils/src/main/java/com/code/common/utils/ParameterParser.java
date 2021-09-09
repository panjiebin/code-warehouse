package com.code.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-10-21 16:24
 */
public class ParameterParser {

    private ParameterParser() {
    }

    /**
     * parse script
     * eg.
     * script = log(:a,:b),params = {"a":"10","b":20} => log(10,10)
     *
     * @param script script
     * @param params params of script
     * @return parsed script
     */
    public static String parse(String script, Map<String, Object> params) {
        if (StringUtils.isBlank(script)) {
            return null;
        }
        return parseParam(script, params, null);
    }

    private static String parseParam(String script, Map<String, Object> params, List<String> paramNames) {
        StringBuilder exp = new StringBuilder();
        for (int index = 0; index < script.length(); index++) {
            char c = script.charAt(index);
            if (c == ':') {
                // named parameter
                int right = firstIndexOfChar(script, index + 1);
                int chopLocation = right < 0 ? script.length() : right;
                String param = script.substring(index + 1, chopLocation);
                if (param.length() == 0) {
                    throw new RuntimeException("Space is not allowed after parameter prefix ':' '"
                            + script + "'");
                }
                if (params != null && !params.containsKey(param)) {
                    throw new RuntimeException("script [" + script + "], params does not contain param [" + param + "]");
                }
                if (params != null) {
                    exp.append(params.get(param));
                }
                if (paramNames != null) {
                    paramNames.add(param);
                }
                index = chopLocation - 1;
            } else {
                exp.append(c);
            }
        }
        return exp.length() != 0 ? exp.toString() : script;
    }

    /**
     * parse script contain param
     * eg.
     * script = log(:a,:b)
     * return => {a,b}
     *
     * @param script script
     * @return parameters of script
     */
    public static List<String> parseParameters(String script) {
        if (StringUtils.isBlank(script)) {
            return Collections.emptyList();
        }
        List<String> params = new ArrayList<>();
        parseParam(script, null, params);
        return params;
    }

    private static int firstIndexOfChar(String expression, int startIndex) {
        String chars = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\";
        int matchAt = -1;
        for (int i = 0; i < chars.length(); i++) {
            int curMatch = expression.indexOf(chars.charAt(i), startIndex);
            if (curMatch >= 0) {
                // first time we find match!
                if (matchAt == -1) {
                    matchAt = curMatch;
                } else {
                    matchAt = Math.min(matchAt, curMatch);
                }
            }
        }
        return matchAt;
    }

}
