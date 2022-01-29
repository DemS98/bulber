package com.demetrio.bulber.engine.bulb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({
    "get_sysinfo"
})
public class System {

    @JsonProperty("get_sysinfo")
    private GetSysinfo getSysinfo;

    /**
     * No args constructor for use in serialization
     * 
     */
    public System() {
    }

    /**
     * 
     * @param getSysinfo
     */
    public System(GetSysinfo getSysinfo) {
        super();
        this.getSysinfo = getSysinfo;
    }

    public GetSysinfo getGetSysinfo() {
        return getSysinfo;
    }

    public void setGetSysinfo(GetSysinfo getSysinfo) {
        this.getSysinfo = getSysinfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        System system = (System) o;
        return Objects.equals(getSysinfo, system.getSysinfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysinfo);
    }

    @Override
    public String toString() {
        return "System{" +
                "getSysinfo=" + getSysinfo +
                '}';
    }
}
