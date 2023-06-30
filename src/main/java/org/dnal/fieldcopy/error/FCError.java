package org.dnal.fieldcopy.error;

public class FCError {
    private String id;
    private String msg;

    public FCError(String id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SbError{" +
                "id='" + id + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
