package com.fortech.model.utils;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidationKey {

    private String hostName;
    private String ipAddress;
    private String ipMac;
    private String timestamp;
    private String start_date;
    private String finish_date;
    private String client;

    public String getHostName() {
        return hostName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getIpMac() {
        return ipMac;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public String getClient() {
        return client;
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

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void generate(GeneratedKey generatedKey, Date startDate, Date endDate) {
        if (generatedKey.notNull()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            this.setHostName(generatedKey.getHostName());
            this.setIpAddress(generatedKey.getIpAddress());
            this.setIpMac(generatedKey.getIpMac());
            this.setTimestamp(generatedKey.getTimestamp());
            this.setStart_date(sdf.format(startDate));
            this.setFinish_date(sdf.format(endDate));
            this.setClient(System.getProperty("user.name"));
        }
    }


    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
