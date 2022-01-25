package com.ionexchange.Others;

public class GetAllPacketModel {
    String no;
    String type;
    String update;

    public GetAllPacketModel(String no, String type, String update) {
        this.no = no;
        this.type = type;
        this.update = update;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
