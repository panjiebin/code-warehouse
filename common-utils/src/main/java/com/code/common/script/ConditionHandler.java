package com.code.common.script;

import com.code.common.utils.ParameterParser;

import java.util.List;
import java.util.Map;

/**
 * Parsing fragments in a script that start with [/~] and end with [~/]
 * parse rule:
 * eg.
 * script: select * from t_person where 1=1 /~ and name = :name ~/ /~ and age > :age ~/
 * 1) params = {"name":"jack","age":20}
 * => select * from t_person where 1=1 and name = :name and age > :age
 * 2) params = {"name":"jack"}
 * => select * from t_person where 1=1 and name = :name
 * 3) params = {}
 * => select * from t_person where 1=1
 *
 * @author Pan Jiebin
 * @date 2021-02-23 15:03
 */
@Order(2)
class ConditionHandler implements ScriptHandler {

    private static final String PREFIX = "/~";
    private static final String SUFFIX = "~/";

    @Override
    public String handle(String script, Map<String, Object> params) {
        if (!script.contains(PREFIX)) {
            return script;
        }
        StringBuilder sb = new StringBuilder(script);
        int start = 0;
        while ((start = sb.indexOf(PREFIX, start)) > 0) {
            int end = sb.indexOf(SUFFIX, start + 2);
            int nextStart = sb.indexOf(PREFIX, start + 2);
            if (validate(end, nextStart)) {
                throw new IllegalArgumentException("script syntax error, [" + PREFIX + ", "
                        + SUFFIX + "] must appear in pairs, position: \n" + sb.substring(start));
            }
            String fragment = sb.substring(start + 2, end);
            List<String> parameters = ParameterParser.parseParameters(fragment);
            if (parameters.isEmpty()) {
                throw new IllegalArgumentException("script syntax error, [" + PREFIX + ", "
                        + SUFFIX + "] must contain [:paramName] parameter expression, position: \n"
                        + sb.substring(start));
            }
            if (params != null && params.keySet().containsAll(parameters)) {
                sb.delete(end, end + 2);
                sb.delete(start, start + 2);
                start -= 2;
            } else {
                sb.delete(start, end + 2);
            }
        }
        return sb.toString();
    }

    private boolean validate(int end, int nextStart) {
        return end < 0 || (nextStart > 0 && nextStart < end);
    }
}
