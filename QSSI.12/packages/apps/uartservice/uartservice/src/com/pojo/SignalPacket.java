package com.pojo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SignalPacket implements Serializable {
    private String signal;
    private Integer canId;
    private Object value;
    private Long timestamp;

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public Integer getCanId() {
        return canId;
    }

    public void setCanId(Integer canId) {
        this.canId = canId;
    }

    public Object getValue() {
        return value;
    }

    public void setData(Object value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public SignalPacket(){};
    public SignalPacket(String signal, Integer canId, Object value){
        this.signal = signal;
        this.canId = canId;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }
    public String toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("signal", this.getSignal());
            jsonObject.put("canId", this.getCanId());
            jsonObject.put("value", this.getValue());
            jsonObject.put("timestamp", this.getTimestamp());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

}
