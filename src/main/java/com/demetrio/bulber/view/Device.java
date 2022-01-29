package com.demetrio.bulber.view;

import com.demetrio.bulber.engine.bulb.Bulb;

import java.util.Objects;

public class Device {

    private String address;
    private Bulb bulb;

    public Device() {
    }

    public Device(String address, Bulb bulb) {
        this.address = address;
        this.bulb = bulb;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Bulb getBulb() {
        return bulb;
    }

    public void setBulb(Bulb bulb) {
        this.bulb = bulb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(address, device.address) && Objects.equals(bulb, device.bulb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, bulb);
    }

    @Override
    public String toString() {
        return "Device{" +
                "address='" + address + '\'' +
                ", bulb=" + bulb +
                '}';
    }
}
