package com.demetrio.bulber.engine.bulb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({
    "system"
})
public class Bulb {

    @JsonProperty("system")
    private System system;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Bulb() {
    }

    /**
     * 
     * @param system
     */
    public Bulb(System system) {
        super();
        this.system = system;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bulb bulb = (Bulb) o;
        return Objects.equals(system, bulb.system);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system);
    }

    @Override
    public String toString() {
        return "Bulb{" +
                "system=" + system +
                '}';
    }
}
