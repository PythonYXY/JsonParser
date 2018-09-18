package json_parser;

import java.util.List;
import java.util.LinkedHashMap;

public class Value {
    private ParserType type;
    private double num;
    private String str;
    private List<Value> array;
    private LinkedHashMap<String, Value> object;

    public ParserType getType() {
        return type;
    }

    public void setType(ParserType type) {
        this.type = type;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public List<Value> getArray() {
        return array;
    }

    public void setArray(List<Value> array) {
        this.array = array;
    }

    public LinkedHashMap<String, Value> getObject() {
        return object;
    }

    public void setObject(LinkedHashMap<String, Value> object) {
        this.object = object;
    }

}
