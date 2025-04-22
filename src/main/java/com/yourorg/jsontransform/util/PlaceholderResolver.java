package com.yourorg.jsontransform.util;

import java.util.*;
import java.util.regex.*;

public class PlaceholderResolver {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final Pattern EACH_PATTERN = Pattern.compile("\\$\\{#each (.+?) as (.+?)}(.*?)\\$\\{\\/each}", Pattern.DOTALL);

    public static String resolve(String template, Map<String, Object> source) {
        template = resolveEach(template, source);
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String expr = matcher.group(1);
            String value = resolveExpression(expr, source);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String resolveEach(String template, Map<String, Object> source) {
        Matcher matcher = EACH_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String listPath = matcher.group(1).trim();
            String alias = matcher.group(2).trim();
            String innerTemplate = matcher.group(3);

            Object listObj = getValueFromPath(source, listPath);
            if (listObj instanceof List<?> list) {
                StringBuilder segment = new StringBuilder();
                for (Object item : list) {
                    Map<String, Object> scope = new HashMap<>(source);
                    scope.put(alias, item);
                    segment.append(resolve(innerTemplate, scope));
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement(segment.toString()));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String resolveExpression(String expr, Map<String, Object> source) {
        if (expr.contains("?:")) {
            String[] parts = expr.split("\\?:", 2);
            Object val = getValueFromPath(source, parts[0].trim());
            return val != null ? val.toString() : parts[1].trim().replaceAll("^['\"]|['\"]$", "");
        }

        if (expr.contains(".join(")) {
            String base = expr.split("\\.join\\(")[0];
            String sep = expr.replaceAll("^.*\\.join\\(['\"](.*)['\"]\\)$", "$1");
            Object val = getValueFromPath(source, base.trim());
            if (val instanceof List<?> list) {
                return String.join(sep, list.stream().map(Object::toString).toList());
            }
            return "";
        }

        Object value = getValueFromPath(source, expr.trim());
        return value != null ? value.toString() : "";
    }

    private static Object getValueFromPath(Object current, String path) {
        String[] parts = path.split("\\.");
        for (String part : parts) {
            if (part.matches(".+\\[\\d+\\]")) {
                String key = part.replaceAll("\\[\\d+\\]", "");
                int index = Integer.parseInt(part.replaceAll(".*\\[(\\d+)\\]", "$1"));

                if (current instanceof Map<?, ?> map) {
                    current = map.get(key);
                }
                if (current instanceof List<?> list && index < list.size()) {
                    current = list.get(index);
                } else {
                    return null;
                }
            } else if (current instanceof Map<?, ?> map) {
                current = map.get(part);
            } else {
                return null;
            }
        }
        return current;
    }
}