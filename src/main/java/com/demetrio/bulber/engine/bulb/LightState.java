package com.demetrio.bulber.engine.bulb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({
    "brightness",
    "color_temp",
    "hue",
    "mode",
    "on_off",
    "saturation"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LightState {

    @JsonProperty("brightness")
    private Integer brightness;
    @JsonProperty("color_temp")
    private Integer colorTemp;
    @JsonProperty("hue")
    private Integer hue;
    @JsonProperty("mode")
    private String mode;
    @JsonProperty("on_off")
    private Integer onOff;
    @JsonProperty("saturation")
    private Integer saturation;

    // request parameters
    @JsonProperty("transition_period")
    private Integer transitionPeriod;
    @JsonProperty("ignore_default")
    private final Integer ignoreDefault;

    /**
     * No args constructor for use in serialization
     * 
     */
    public LightState() {
        ignoreDefault = 1;
    }

    /**
     * 
     * @param mode
     * @param saturation
     * @param brightness
     * @param colorTemp
     * @param hue
     * @param onOff
     */
    public LightState(Integer brightness, Integer colorTemp, Integer hue, String mode, Integer onOff, Integer saturation,
                      Integer transitionPeriod) {
        super();
        this.brightness = brightness;
        this.colorTemp = colorTemp;
        this.hue = hue;
        this.mode = mode;
        this.onOff = onOff;
        this.saturation = saturation;
        this.transitionPeriod = transitionPeriod;
        ignoreDefault = 1;
    }


    public Integer getBrightness() {
        return brightness;
    }


    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }


    public Integer getColorTemp() {
        return colorTemp;
    }


    public void setColorTemp(Integer colorTemp) {
        this.colorTemp = colorTemp;
    }


    public Integer getHue() {
        return hue;
    }


    public void setHue(Integer hue) {
        this.hue = hue;
    }


    public String getMode() {
        return mode;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }


    public Integer getOnOff() {
        return onOff;
    }


    public void setOnOff(Integer onOff) {
        this.onOff = onOff;
    }


    public Integer getSaturation() {
        return saturation;
    }


    public void setSaturation(Integer saturation) {
        this.saturation = saturation;
    }

    public Integer getTransitionPeriod() {
        return transitionPeriod;
    }

    public void setTransitionPeriod(Integer transitionPeriod) {
        this.transitionPeriod = transitionPeriod;
    }

    @Override
    public String toString() {
        return "LightState{" +
                "brightness=" + brightness +
                ", colorTemp=" + colorTemp +
                ", hue=" + hue +
                ", mode='" + mode + '\'' +
                ", onOff=" + onOff +
                ", saturation=" + saturation +
                ", transitionPeriod=" + transitionPeriod +
                ", ignoreDefault=" + ignoreDefault +
                '}';
    }
}
