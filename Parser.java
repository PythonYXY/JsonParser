package json_parser;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static json_parser.ParserResult.*;
import static json_parser.ParserType.*;

public class Parser {
    private ArrayDeque<Character> json;
    private Value value;

    public Parser() {
        value = new Value();
        this.json = new ArrayDeque<>();
    }

    public Parser(String json) {
        value = new Value();
        this.json = new ArrayDeque<>();
        setJson(json);
    }

    public Value getValue() {
        return value;
    }

    public void setJson(String json) {
        this.json.clear();
        for (int i = 0; i < json.length(); ++i) this.json.add(json.charAt(i)); // can't accept unicode characer for now.
    }

    public void parseWhiteSpace() {
        if (this.json.isEmpty()) return;
        while (this.json.peek() == ' ' ||
               this.json.peek() == '\t' ||
               this.json.peek() == '\n' ||
               this.json.peek() == '\r')
            this.json.poll();
    }

    public ParserResult parseLiteral(String literal, ParserType type, Value v) {
        assert json.peek() == literal.charAt(0);
        json.pop();
        for (int i = 1; i < literal.length(); ++i) {
            if (json.isEmpty() || json.poll() != literal.charAt(i)) {
                return PARSE_INVALID_VALUE;
            }
        }
        v.setType(type);
        return PARSE_OK;
    }

    public ParserResult parseNumber(Value v) {
        if (json.isEmpty()) return PARSE_EXPECT_VALUE;
        StringBuilder sb = new StringBuilder();
        if (json.peek() == '-') sb.append(json.pop());

        if (!json.isEmpty() && json.peek() == '0') {
            sb.append(json.pop());
        } else if (!json.isEmpty() && Character.isDigit(json.peek())) {
            sb.append(json.pop());
            while (!json.isEmpty() && Character.isDigit(json.peek())) sb.append(json.pop());
        } else return PARSE_INVALID_VALUE;

        if (!json.isEmpty() && json.peek() == '.') {
            sb.append(json.pop());
            if (!json.isEmpty() && Character.isDigit(json.peek())) {
                while (!json.isEmpty() && Character.isDigit(json.peek())) sb.append(json.pop());
            } else {
                return PARSE_INVALID_VALUE;
            }
        }

        if (!json.isEmpty() && (json.peek() == 'e' || json.peek() == 'E')) {
            sb.append(json.pop());
            if (!json.isEmpty() && (json.peek() == '+' || json.peek() == '-')) sb.append((json.pop()));
            if (!json.isEmpty() && Character.isDigit(json.peek())) {
                while (!json.isEmpty() && Character.isDigit(json.peek())) sb.append(json.pop());
            } else {
                return PARSE_INVALID_VALUE;
            }
        }
        double num = Double.valueOf(sb.toString());
        if (num == Double.NEGATIVE_INFINITY || num == Double.POSITIVE_INFINITY) return PARSE_NUMBER_TOO_BIG;
        v.setNum(num);
        v.setType(NUMBER);
        return PARSE_OK;
    }
    public ParserResult parseStringRaw(StringBuilder sb) {
        assert json.peek() == '\"';
        json.pop();
        while (true) {
            if (json.isEmpty()) {
                return PARSE_MISS_QUOTATION_MARK;
            }
            switch (json.peek()) {
                case '\"':
                    json.pop();
                    return PARSE_OK;
                case '\\':
                    json.pop();
                    switch (json.peek()) {
                        case 'n': sb.append('\n'); break;
                        case '\"': sb.append('\"'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case '\\': sb.append('\\'); break;
                        case 'f': sb.append('\f'); break;
                        default: return PARSE_INVALID_STRING_ESCAPE;
                    }
                    json.pop();
                    break;
                default:
                    if (json.peek() < 0x20) return PARSE_INVALID_STRING_CHAR;
                    sb.append(json.pop());
            }
        }
    }

    public ParserResult parseString(Value v) {
        StringBuilder sb = new StringBuilder();
        ParserResult ret;
        if ((ret = parseStringRaw(sb)) == PARSE_OK) {
            v.setType(STRING);
            v.setStr(sb.toString());
        }
        return ret;
    }

    public ParserResult parseArray(Value v) {
        assert json.peek() == '[';
        ParserResult ret;
        v.setArray(new LinkedList<>());
        json.pop();
        parseWhiteSpace();
        if (!json.isEmpty() && json.peek() == ']') {
            json.pop();
            v.setType(ARRAY);
            return PARSE_OK;
        }
        while (true) {
            Value newValue = new Value();
            if ((ret = parseValue(newValue)) != PARSE_OK) break;
            v.getArray().add(newValue);
            parseWhiteSpace();
            if (!json.isEmpty() && json.peek() == ',') {
                json.pop();
                parseWhiteSpace();
            } else if (!json.isEmpty() && json.peek() == ']') {
                json.pop();
                v.setType(ARRAY);
                return PARSE_OK;
            } else {
                ret = PARSE_MISS_COMMA_OR_SQUARE_BRACKET;
                break;
            }
        }
        return ret;
    }

    public ParserResult parseObject(Value v) {
        assert json.peek() == '{';
        ParserResult ret;
        v.setObject(new LinkedHashMap<>());
        json.pop();
        parseWhiteSpace();
        if (!json.isEmpty() && json.peek() == '}') {
            json.pop();
            v.setType(OBJECT);
            return PARSE_OK;
        }
        while (true) {
            if (json.isEmpty() || json.peek() != '"') {
                ret = PARSE_MISS_KEY;
                break;
            }
            LinkedHashMap<String, Value> map = v.getObject();
            StringBuilder sb = new StringBuilder();
            Value newValue = new Value();
            if ((ret = parseStringRaw(sb)) != PARSE_OK) break;
            parseWhiteSpace();
            if (json.isEmpty() || json.peek() != ':') {
                ret = PARSE_MISS_COLON;
                break;
            }
            json.pop();
            parseWhiteSpace();
            if ((ret = parseValue(newValue)) != PARSE_OK) break;
            map.put(sb.toString(), newValue);
            parseWhiteSpace();

            if (!json.isEmpty() && json.peek() == ',') {
                json.pop();
                parseWhiteSpace();
            } else if (!json.isEmpty() && json.peek() == '}') {
                json.pop();
                v.setType(OBJECT);
                return PARSE_OK;
            } else {
                ret = PARSE_MISS_COMMA_OR_CURLY_BRACKET;
                break;
            }
        }
        v.setType(NULL);
        return ret;
    }

    public ParserResult parseValue(Value v) {
        switch(json.peek()) {
            case 't': return parseLiteral("true", TRUE, v);
            case 'f': return parseLiteral("false", FALSE, v);
            case 'n': return parseLiteral("null", NULL, v);
            case '\"': return parseString(v);
            case '[': return parseArray(v);
            case '{': return parseObject(v);
            default:
                return parseNumber(v);
                // if (json.peek() == null) return PARSE_EXPECT_VALUE;
                // return PARSE_INVALID_VALUE;
        }
    }

    public ParserResult parse() {
        ParserResult ret;
        value.setType(NULL);
        parseWhiteSpace();
        if ((ret = parseValue(value)) == PARSE_OK) {
            parseWhiteSpace();
            if (!json.isEmpty()) {
                value.setType(NULL);
                ret = PARSE_ROOT_NOT_SINGULAR;
            }
        }
        return ret;
    }
}
