package be.fluid_it.tools.rundeck.plugins.util;

import com.dtolabs.rundeck.core.execution.ExecutionContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpandUtil {
    public static String expand(String url, ExecutionContext executionContext) {
        String[] parts = url.split("\\?", 2);
        List<String> expandedParts = new LinkedList<String>();
        for (String part : parts) {
            expandedParts.add(expandPart(part, executionContext));

        }
        expandedParts.toArray(new String[expandedParts.size()]);
        return join("?", expandedParts);
    }

    private static String expandPart(String part, ExecutionContext executionContext) {
        String pattern = "(\\$\\{(job|option)\\.([^}]+?(\\.value)?)\\})";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(part);
        while (matcher.find()) {
            String value = null;
            switch (matcher.group(2)) {
                case "option":
                    if (".value".equals(matcher.group(4))) {
                        String group3 = matcher.group(3);
                        String optionName = group3.substring(0, group3.length() - ".value".length());
                        value = executionContext.getDataContext().get("option").get(optionName);
                    }
                    break;
                case "job":
                    //TODO Not yet needed
                    break;
                default:
            }
            if (value == null) {
                value = "";
            }
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            part = subexpr.matcher(part).replaceAll(value);
        }
        return part;
    }

    private static String expand(String text, Map<String, String> map) {
        String pattern = "\\$\\{([A-Za-z0-9]+)\\}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(text);
        while (matcher.find()) {
            String value = map.get(matcher.group(1));
            if (value == null) {
                value = "";
            }
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            text = subexpr.matcher(text).replaceAll(value);
        }
        return text;
    }

    private static String join(String join, List<String> strings) {
        if (strings == null || strings.size() == 0) {
            return "";
        } else if (strings.size() == 1) {
            return strings.get(0);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strings.get(0));
            for (int i = 1; i < strings.size(); i++) {
                sb.append(join).append(strings.get(i));
            }
            return sb.toString();
        }
    }
}
