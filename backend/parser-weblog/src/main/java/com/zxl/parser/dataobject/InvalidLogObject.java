package com.zxl.parser.dataobject;

public class InvalidLogObject implements ParsedDataObject {
    private String event; //解析错误的原因

    public InvalidLogObject(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
