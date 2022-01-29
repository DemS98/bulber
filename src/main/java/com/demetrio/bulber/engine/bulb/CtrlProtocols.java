package com.demetrio.bulber.engine.bulb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({
    "name",
    "version"
})
public class CtrlProtocols {

    @JsonProperty("name")
    private String name;
    @JsonProperty("version")
    private String version;

    /**
     * No args constructor for use in serialization
     * 
     */
    public CtrlProtocols() {
    }

    /**
     * 
     * @param name
     * @param version
     */
    public CtrlProtocols(String name, String version) {
        super();
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CtrlProtocols that = (CtrlProtocols) o;
        return Objects.equals(name, that.name) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }

    @Override
    public String toString() {
        return "CtrlProtocols{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
