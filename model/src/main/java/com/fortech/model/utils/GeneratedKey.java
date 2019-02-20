package com.fortech.model.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class GeneratedKey {

    private String hostName;
    private String ipAddress;
    private String ipMac;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public String getHostName() {
        return hostName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getIpMac() {
        return ipMac;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setIpMac(String ipMac) {
        this.ipMac = ipMac;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public void generateFromString(String json1) throws JsonParseException {

        try {
            GeneratedKey generatedKey = new Gson().fromJson(json1, GeneratedKey.class);

            this.setHostName(generatedKey.getHostName());
            this.setIpAddress(generatedKey.getIpAddress());
            this.setIpMac(generatedKey.getIpMac());
            this.setTimestamp(generatedKey.getTimestamp());

        } catch (JsonParseException e) {
            System.out.println("String cannot be parsed " + json1);
        }
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public boolean notNull() {
        return (this.getHostName() != null) &&
                (this.getIpAddress() != null) &&
                (this.getIpMac() != null) &&
                (this.getTimestamp() != null);
    }

}
