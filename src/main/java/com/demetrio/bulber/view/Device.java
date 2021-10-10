package com.demetrio.bulber.view;

public class Device {

    private String deviceName;
    private String address;
    private boolean on;
    private int minTemperature;
    private int maxTemperature;
    private int defaultTemperature;

    public Device() {}

    public Device(String deviceName, String address, boolean on) {
        this.deviceName = deviceName;
        this.address = address;
        this.on = on;
    }

    public Device(String deviceName, String address, boolean on, int minTemperature, int maxTemperature, int defaultTemperature) {
        this.deviceName = deviceName;
        this.address = address;
        this.on = on;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.defaultTemperature = defaultTemperature;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getDefaultTemperature() {
        return defaultTemperature;
    }

    public void setDefaultTemperature(int defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }
}
