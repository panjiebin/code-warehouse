package com.code.common.script;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parsing fragments in a script that start with [#{] and end with [}]
 * parse rule:
 * eg.
 * script: select * from t_person where 1=1 #{template}
 * 1) params = {"template":"and name = :name and age > :age"}
 * => select * from t_person where 1=1 and name = :name and age > :age
 * 2) params = {}
 * => select * from t_person where 1=1
 *
 * @author Pan Jiebin
 * @date 2021-02-23 15:08
 */
@Order(1)
class TemplateHandler implements ScriptHandler {

    private static final String PREFIX = "#{";
    private static final String SUFFIX = "}";

    @Override
    public String handle(String script, Map<String, Object> params) {
        if (!script.contains(PREFIX)) {
            return script;
        }
        StringBuilder sb = new StringBuilder(script);
        int start = 0;
        Set<String> templates = new HashSet<>();
        while ((start = sb.indexOf(PREFIX, start)) > 0) {
            int end = sb.indexOf(SUFFIX, start + 2);
            int nextStart = sb.indexOf(PREFIX, start + 2);
            if (validate(end, nextStart)) {
                throw new IllegalArgumentException("script syntax error, [" + PREFIX + ", "
                        + SUFFIX + "] must appear in pairs, position: \n" + sb.substring(start));
            }
            String templateName = sb.substring(start + 2, end).trim();
            templates.add(templateName);
            if (params != null && params.containsKey(templateName)) {
                String template = String.valueOf(params.get(templateName));
                sb.replace(start, end + 1, template);
                start += template.length();
            } else {
                sb.delete(start, end + 1);
            }
        }
        if (params != null) {
            for (String template : templates) {
                params.remove(template);
            }
        }
        return sb.toString();
    }

    private boolean validate(int end, int nextStart) {
        return end < 0 || (nextStart > 0 && nextStart < end);
    }
}
