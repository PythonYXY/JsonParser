package json_parser;


public class TestException extends Exception {
    private String msg;

    public void printMsg() {
        System.out.println(msg);
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
