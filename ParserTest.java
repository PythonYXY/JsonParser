package json_parser;

import json_parser.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import static json_parser.ParserType.*;
import static json_parser.ParserResult.*;

import java.util.LinkedHashMap;
import java.util.List;

public class ParserTest {
    private static int testCount = 0;
    private static int testPass = 0;

    public static void expectEqualString(String expect, String actual) throws TestException {
        testCount++;
        if (expect.equals(actual)) testPass++;
        else {
            TestException e = new TestException();
            e.setMsg("expect: " + expect + " : " + "actual: " + actual);
            throw e;
        }
    }

    public static void expectEqualChar(char expect, char actual) throws TestException {
        testCount++;
        if (expect == actual) testPass++;
        else {
            TestException e = new TestException();
            e.setMsg("expect: " + expect + " : " + "actual: " + actual);
            throw e;
        }
    }

    public static void expectEqualType(ParserType expect, ParserType actual) throws TestException {
        testCount++;
        if (expect == actual) testPass++;
        else {
            TestException e = new TestException();
            e.setMsg("expect: " + expect + " : " + "actual: " + actual);
            throw e;
        }
    }

    public static void expectEqualResult(ParserResult expect, ParserResult actual) throws TestException {
        testCount++;
        if (expect == actual) testPass++;
        else {
            TestException e = new TestException();
            e.setMsg("expect: " + expect + " : " + "actual: " + actual);
            throw e;
        }
    }

    public static void expectEqualDouble(Double expect, double actual) throws TestException {
        testCount++;
        if (expect == actual) testPass++;
        else {
            TestException e = new TestException();
            e.setMsg("expect: " + expect + " : " + "actual: " + actual);
            throw e;
        }
    }


    public static void testNumber(Parser p, double expect, String json) {
        p.setJson(json);
        try {
            expectEqualResult(PARSE_OK, p.parse());
            expectEqualType(NUMBER, p.getValue().getType());
            expectEqualDouble(expect, p.getValue().getNum());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testString(Parser p, String expect, String json) {
        p.setJson(json);
        try {
            expectEqualResult(PARSE_OK, p.parse());
            expectEqualType(STRING, p.getValue().getType());
            expectEqualString(expect, p.getValue().getStr());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testParseObject() {
        Parser p = new Parser();

        try {
            p.setJson("{  }");
            expectEqualResult(PARSE_OK, p.parse());
            expectEqualType(OBJECT, p.getValue().getType());
            expectEqualDouble((double)0, p.getValue().getObject().size());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }

        try {
            p.setJson(" { \"n\" : null , \"f\" : false , \"t\" : true , \"i\" : 123 , \"s\" : \"abc\", \"a\" : [ 1, 2, 3 ],\"o\" : { \"1\" : 1, \"2\" : 2, \"3\" : 3 } }");
            expectEqualResult(PARSE_OK, p.parse());

            Value v = p.getValue();
            LinkedHashMap<String, Value> map = v.getObject();
            expectEqualType(OBJECT, v.getType());
            expectEqualDouble(7.0, (double)map.size());

            String[] strArray = map.keySet().toArray(new String[0]);
            Value[] valueArray = map.values().toArray(new Value[0]);

            expectEqualString("n", strArray[0]);
            expectEqualType(NULL, valueArray[0].getType());

            expectEqualString("f", strArray[1]);
            expectEqualType(FALSE, valueArray[1].getType());

            expectEqualString("t", strArray[2]);
            expectEqualType(TRUE, valueArray[2].getType());

            expectEqualString("i", strArray[3]);
            expectEqualType(NUMBER, valueArray[3].getType());
            expectEqualDouble(123.0, valueArray[3].getNum());

            expectEqualString("s", strArray[4]);
            expectEqualType(STRING, valueArray[4].getType());
            expectEqualString("abc", valueArray[4].getStr());

            expectEqualString("a", strArray[5]);
            expectEqualType(ARRAY, valueArray[5].getType());
            expectEqualDouble(3.0, (double)valueArray[5].getArray().size());
            for (int i = 0; i < 3; ++i) {
                expectEqualType(NUMBER, valueArray[5]
                                        .getArray()
                                        .get(i)
                                        .getType());
                expectEqualDouble(i + 1.0, valueArray[5]
                                                  .getArray()
                                                  .get(i)
                                                  .getNum());
            }

            expectEqualString("o", strArray[6]);
            expectEqualType(OBJECT, valueArray[6].getType());
            expectEqualDouble(3.0, (double)valueArray[6].getObject().size());

            LinkedHashMap<String, Value> newHashMap = valueArray[6].getObject();
            String[] newStrArray = newHashMap.keySet().toArray(new String[0]);
            Value[] newValArray = newHashMap.values().toArray(new Value[0]);
            for (int i = 0; i < 3; ++i) {
                expectEqualChar((char)('1' + i), newStrArray[i].charAt(0));
                expectEqualType(NUMBER, newValArray[i].getType());
                expectEqualDouble(i + 1.0, newValArray[i].getNum());
            }
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testParseNumber() {
        Parser p = new Parser();
        testNumber(p, 0.0, "0");
        testNumber(p, 0.0, "-0");
        testNumber(p, 0.0, "-0.0");
        testNumber(p, 1.0, "1");
        testNumber(p, -1.0, "-1");
        testNumber(p, 1.5, "1.5");
        testNumber(p, -1.5, "-1.5");
        testNumber(p, 3.1416, "3.1416");
        testNumber(p, 1E10, "1E10");
        testNumber(p, 1e10, "1e10");
        testNumber(p, 1E+10, "1E+10");
        testNumber(p, 1E-10, "1E-10");
        testNumber(p, -1E10, "-1E10");
        testNumber(p, -1e10, "-1e10");
        testNumber(p, -1E+10, "-1E+10");
        testNumber(p, -1E-10, "-1E-10");
        testNumber(p, 1.234E+10, "1.234E+10");
        testNumber(p, 1.234E-10, "1.234E-10");
        testNumber(p, 0.0, "1e-10000"); /* must underflow */

        testNumber(p, 1.0000000000000002, "1.0000000000000002"); /* the smallest number > 1 */
        testNumber(p,  4.9406564584124654e-324, "4.9406564584124654e-324"); /* minimum denormal */
        testNumber(p, -4.9406564584124654e-324, "-4.9406564584124654e-324");
        testNumber(p,  2.2250738585072009e-308, "2.2250738585072009e-308");  /* Max subnormal double */
        testNumber(p, -2.2250738585072009e-308, "-2.2250738585072009e-308");
        testNumber(p,  2.2250738585072014e-308, "2.2250738585072014e-308");  /* Min normal positive double */
        testNumber(p, -2.2250738585072014e-308, "-2.2250738585072014e-308");
        testNumber(p,  1.7976931348623157e+308, "1.7976931348623157e+308");  /* Max double */
        testNumber(p, -1.7976931348623157e+308, "-1.7976931348623157e+308");
    }

    public static void testParseString() {
        Parser p = new Parser();
        testString(p, "", "\"\"");
        testString(p, "Hello", "\"Hello\"");

        testString(p, "Hello\nWorld", "\"Hello\\nWorld\"");
        testString(p, "\" \\ / \b \f \n \r \t", "\"\\\" \\\\ \\/ \\b \\f \\n \\r \\t\"");
    }

    public static void testParseArray() {
        Parser p = new Parser("[ ]");

        try {
            expectEqualResult(PARSE_OK, p.parse());
            expectEqualType(ARRAY, p.getValue().getType());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }

        p.setJson("[ null , false , true , 123 , \"abc\" ]");

        try {

            expectEqualResult(PARSE_OK, p.parse());

            Value value = p.getValue();
            List<Value> array = value.getArray();
            expectEqualType(ARRAY, value.getType());
            expectEqualDouble((double)5, (double)array.size());

            expectEqualType(NULL, array.get(0).getType());
            expectEqualType(FALSE, array.get(1).getType());
            expectEqualType(TRUE, array.get(2).getType());
            expectEqualDouble(123.0, array.get(3).getNum());
            expectEqualString("abc", array.get(4).getStr());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }

        p.setJson("[ [ ] , [ 0 ] , [ 0 , 1 ] , [ 0 , 1 , 2 ] ]");

        try {

            expectEqualResult(PARSE_OK, p.parse());

            Value value = p.getValue();
            List<Value> array = value.getArray();
            expectEqualType(ARRAY, value.getType());
            expectEqualDouble((double)4, array.size());

            for (int i = 0; i < 4; ++i) {
                Value tmpValue = array.get(i);
                List<Value> tmpArray = tmpValue.getArray();

                expectEqualType(ARRAY, tmpValue.getType());
                expectEqualDouble((double)i, tmpArray.size());

                for (int j = 0; j < i; ++j) {
                    expectEqualType(NUMBER, tmpArray.get(j).getType());
                    expectEqualDouble((double)j, tmpArray.get(j).getNum());
                }

            }
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testError(ParserResult error, String json) {
        Parser p = new Parser(json);
        try {
            expectEqualResult(error, p.parse());
            expectEqualType(NULL, p.getValue().getType());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testParseTrue() {
        Parser p = new Parser("true");
        try {
            expectEqualResult(PARSE_OK, p.parse());
            expectEqualType(TRUE, p.getValue().getType());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testParseNull() {
        Parser p = new Parser("null");
        try {
            expectEqualResult(PARSE_OK, p.parse());
            expectEqualType(NULL, p.getValue().getType());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testParseFalse() {
        Parser p = new Parser("false");
        try {
            expectEqualResult(PARSE_OK, p.parse());
            expectEqualType(FALSE, p.getValue().getType());
        } catch (TestException e) {
            e.printMsg();
            e.printStackTrace();
        }
    }

    public static void testParseInvalidValue() {
        testError(PARSE_INVALID_VALUE, "nul");
        testError(PARSE_INVALID_VALUE, "?");

        testError(PARSE_INVALID_VALUE, "+0");
        testError(PARSE_INVALID_VALUE, "+1");
        testError(PARSE_INVALID_VALUE, ".123");
        testError(PARSE_INVALID_VALUE, "1.");
        testError(PARSE_INVALID_VALUE, "INF");
        testError(PARSE_INVALID_VALUE, "inf");
        testError(PARSE_INVALID_VALUE, "NAN");
        testError(PARSE_INVALID_VALUE, "nan");

        testError(PARSE_INVALID_VALUE, "[1,]");
        testError(PARSE_INVALID_VALUE, "[\"a\", nul]");
    }

    public static void testParseRootNotSingular() {
        testError(PARSE_ROOT_NOT_SINGULAR, "null x");
        testError(PARSE_ROOT_NOT_SINGULAR, "0123");
        testError(PARSE_ROOT_NOT_SINGULAR, "0x0");
        testError(PARSE_ROOT_NOT_SINGULAR, "0x123");
    }

    public static void testParseNumberTooBig() {
        testError(PARSE_NUMBER_TOO_BIG, "1e309");
        testError(PARSE_NUMBER_TOO_BIG, "-1e309");
    }

    public static void testParseMissingQuotationMark() {
        testError(PARSE_MISS_QUOTATION_MARK, "\"");
        testError(PARSE_MISS_QUOTATION_MARK, "\"abc");
    }

    public static void testParseInvalidStringEscape() {
        testError(PARSE_INVALID_STRING_ESCAPE, "\"\\v\"");
        testError(PARSE_INVALID_STRING_ESCAPE, "\"\\'\"");
        testError(PARSE_INVALID_STRING_ESCAPE, "\"\\0\"");
        testError(PARSE_INVALID_STRING_ESCAPE, "\"\\x12\"");
    }

    public static void testParseInvalidStringChar() {
        testError(PARSE_INVALID_STRING_CHAR, "\"\u0001\"");
        testError(PARSE_INVALID_STRING_CHAR, "\"\u001f\"");
    }

    public static void testParseMissCommaOrSquareBracket() {
        testError(PARSE_MISS_COMMA_OR_SQUARE_BRACKET, "[1");
        testError(PARSE_MISS_COMMA_OR_SQUARE_BRACKET, "[1}");
        testError(PARSE_MISS_COMMA_OR_SQUARE_BRACKET, "[1 2]");
        testError(PARSE_MISS_COMMA_OR_SQUARE_BRACKET, "[[]");
    }

    public static void testParseMisskey() {
        testError(PARSE_MISS_KEY, "{:1,");
        testError(PARSE_MISS_KEY, "{1:1,");
        testError(PARSE_MISS_KEY, "{true:1,");
        testError(PARSE_MISS_KEY, "{false:1,");
        testError(PARSE_MISS_KEY, "{null:1,");
        testError(PARSE_MISS_KEY, "{[]:1,");
        testError(PARSE_MISS_KEY, "{{}:1,");
        testError(PARSE_MISS_KEY, "{\"a\":1,");
    }

    public static void testParseMissColon() {
        testError(PARSE_MISS_COLON, "{\"a\"}");
        testError(PARSE_MISS_COLON, "{\"a\", \"b\"}");
    }

    public static void testParseMissCommaOrCurlyBracket() {
        testError(PARSE_MISS_COMMA_OR_CURLY_BRACKET, "{\"a\": 1");
        testError(PARSE_MISS_COMMA_OR_CURLY_BRACKET, "{\"a\":1]");
        testError(PARSE_MISS_COMMA_OR_CURLY_BRACKET, "{\"a\":1 \"b\"}");
        testError(PARSE_MISS_COMMA_OR_CURLY_BRACKET, "{\"a\":{}");
    }

    public static void testStringifyValue() {
        Parser p = new Parser();
        p.setJson("{\n" +
                "\"employees\": [\n" +
                "{ \"firstName\":\"Bill\" , \"lastName\":\"Gates\" },\n" +
                "{ \"firstName\":\"George\" , \"lastName\":\"Bush\" },\n" +
                "{ \"firstName\":\"Thomas\" , \"lastName\":\"Carter\" }\n" +
                "]\n" +
                "}");

        if (p.parse() != PARSE_OK) {
            System.out.println("Parsing failed.");
        };
        System.out.println(Generator.stringifyValue(p.getValue()));

    }

    public static void main(String[] array) {
        testParseFalse();
        testParseTrue();
        testParseNull();
        testParseNumber();
        testParseInvalidValue();
        testParseRootNotSingular();
        testParseNumberTooBig();
        testParseString();
        testParseInvalidStringEscape();
        testParseMissingQuotationMark();
        testParseInvalidStringChar();
        testParseArray();
        testParseMissCommaOrSquareBracket();
        testParseObject();
        testParseMisskey();
        testParseMissColon();
        testParseMissCommaOrCurlyBracket();

        testStringifyValue();

        System.out.printf("%d/%d (%3.2f) passed.\n", testPass, testCount, testPass * 100.0 / testCount);
    }
}

