package json_parser;

import java.util.Map;

public class Generator {
    public static String stringifyValue(Value value) {
        return stringifyValue(value, 0, false);
    }

    public static String stringifyString(String valueStr) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < valueStr.length(); ++i) {
            switch (valueStr.charAt(i)) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(valueStr.charAt(i));
            }
        }
        sb.append('"');
        return sb.toString();
    }


    public static String appendTabs(int tabs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabs; ++i) sb.append('\t');
        return sb.toString();
    }

    public static String stringifyValue(Value value, int tabs, boolean colon) {
        StringBuilder sb = new StringBuilder();
        StringBuilder ret = new StringBuilder();
        switch (value.getType()) {
            case NULL:
                if (!colon) ret.append(appendTabs(tabs));
                ret.append("null");
                return ret.toString();
            case FALSE:
                if (!colon) ret.append(appendTabs(tabs));
                ret.append("null");
                return ret.toString();
            case TRUE:
                if (!colon) ret.append(appendTabs(tabs));
                ret.append("true");
                return ret.toString();
            case NUMBER:
                if (!colon) ret.append(appendTabs(tabs));
                double tmpNum = (double)(int)value.getNum();
                ret.append(tmpNum == value.getNum() ? Integer.toString((int)tmpNum) : Double.toString(value.getNum()));
                return ret.toString();
            case STRING:
                if (!colon) ret.append(appendTabs(tabs));
                ret.append(stringifyString(value.getStr()));
                return ret.toString();
            case ARRAY:
                sb.append("[\n");
                for (Value v : value.getArray()) {
                    sb.append(stringifyValue(v, tabs + 1, false));
                    sb.append(",\n");
                }
                if (sb.charAt(sb.length() - 2) == ',') sb.deleteCharAt(sb.length() - 2);
                sb.append(appendTabs(tabs));
                sb.append(']');
                return sb.toString();
            case OBJECT:
                if (!colon) sb.append(appendTabs(tabs));
                sb.append("{\n");
                for (Map.Entry<String, Value> entry : value.getObject().entrySet()) {
                    sb.append(appendTabs(tabs + 1));
                    sb.append(stringifyString(entry.getKey()));
                    sb.append(": ");
                    sb.append(stringifyValue(entry.getValue(), tabs + 1, true));
                    sb.append(",\n");
                }
                if (sb.charAt(sb.length() - 2) == ',') sb.deleteCharAt(sb.length() - 2);
                sb.append(appendTabs(tabs));
                sb.append('}');
                return sb.toString();
        }
        return "";
    }

}
