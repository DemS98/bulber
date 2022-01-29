package com.demetrio.bulber.engine.bulb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({
    "active_mode",
    "alias",
    "ctrl_protocols",
    "description",
    "dev_state",
    "deviceId",
    "disco_ver",
    "err_code",
    "hwId",
    "hw_ver",
    "is_color",
    "is_dimmable",
    "is_factory",
    "is_variable_color_temp",
    "latitude_i",
    "light_state",
    "longitude_i",
    "mic_mac",
    "mic_type",
    "model",
    "oemId",
    "rssi",
    "status",
    "sw_ver"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetSysinfo {

    @JsonProperty("active_mode")
    private String activeMode;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("ctrl_protocols")
    private CtrlProtocols ctrlProtocols;
    @JsonProperty("description")
    private String description;
    @JsonProperty("dev_state")
    private String devState;
    @JsonProperty("deviceId")
    private String deviceId;
    @JsonProperty("disco_ver")
    private String discoVer;
    @JsonProperty("err_code")
    private Integer errCode;
    @JsonProperty("hwId")
    private String hwId;
    @JsonProperty("hw_ver")
    private String hwVer;
    @JsonProperty("is_color")
    private Integer isColor;
    @JsonProperty("is_dimmable")
    private Integer isDimmable;
    @JsonProperty("is_factory")
    private Boolean isFactory;
    @JsonProperty("is_variable_color_temp")
    private Integer isVariableColorTemp;
    @JsonProperty("latitude_i")
    private Integer latitudeI;
    @JsonProperty("light_state")
    private LightState lightState;
    @JsonProperty("longitude_i")
    private Integer longitudeI;
    @JsonProperty("mic_mac")
    private String micMac;
    @JsonProperty("mic_type")
    private String micType;
    @JsonProperty("model")
    private String model;
    @JsonProperty("oemId")
    private String oemId;
    @JsonProperty("rssi")
    private Integer rssi;
    @JsonProperty("status")
    private String status;
    @JsonProperty("sw_ver")
    private String swVer;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GetSysinfo() {
    }

    /**
     * 
     * @param isDimmable
     * @param rssi
     * @param hwVer
     * @param ctrlProtocols
     * @param description
     * @param longitudeI
     * @param deviceId
     * @param discoVer
     * @param micType
     * @param devState
     * @param lightState
     * @param latitudeI
     * @param swVer
     * @param errCode
     * @param isColor
     * @param activeMode
     * @param alias
     * @param oemId
     * @param model
     * @param hwId
     * @param isFactory
     * @param isVariableColorTemp
     * @param micMac
     * @param status
     */
    public GetSysinfo(String activeMode, String alias, CtrlProtocols ctrlProtocols, String description, String devState, String deviceId, String discoVer, Integer errCode, String hwId, String hwVer, Integer isColor, Integer isDimmable, Boolean isFactory, Integer isVariableColorTemp, Integer latitudeI, LightState lightState, Integer longitudeI, String micMac, String micType, String model, String oemId, Integer rssi, String status, String swVer) {
        super();
        this.activeMode = activeMode;
        this.alias = alias;
        this.ctrlProtocols = ctrlProtocols;
        this.description = description;
        this.devState = devState;
        this.deviceId = deviceId;
        this.discoVer = discoVer;
        this.errCode = errCode;
        this.hwId = hwId;
        this.hwVer = hwVer;
        this.isColor = isColor;
        this.isDimmable = isDimmable;
        this.isFactory = isFactory;
        this.isVariableColorTemp = isVariableColorTemp;
        this.latitudeI = latitudeI;
        this.lightState = lightState;
        this.longitudeI = longitudeI;
        this.micMac = micMac;
        this.micType = micType;
        this.model = model;
        this.oemId = oemId;
        this.rssi = rssi;
        this.status = status;
        this.swVer = swVer;
    }


    public String getActiveMode() {
        return activeMode;
    }


    public void setActiveMode(String activeMode) {
        this.activeMode = activeMode;
    }


    public String getAlias() {
        return alias;
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }


    public CtrlProtocols getCtrlProtocols() {
        return ctrlProtocols;
    }


    public void setCtrlProtocols(CtrlProtocols ctrlProtocols) {
        this.ctrlProtocols = ctrlProtocols;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getDevState() {
        return devState;
    }


    public void setDevState(String devState) {
        this.devState = devState;
    }


    public String getDeviceId() {
        return deviceId;
    }


    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getDiscoVer() {
        return discoVer;
    }


    public void setDiscoVer(String discoVer) {
        this.discoVer = discoVer;
    }


    public Integer getErrCode() {
        return errCode;
    }


    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }


    public String getHwId() {
        return hwId;
    }


    public void setHwId(String hwId) {
        this.hwId = hwId;
    }


    public String getHwVer() {
        return hwVer;
    }


    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }


    public Integer getIsColor() {
        return isColor;
    }


    public void setIsColor(Integer isColor) {
        this.isColor = isColor;
    }


    public Integer getIsDimmable() {
        return isDimmable;
    }


    public void setIsDimmable(Integer isDimmable) {
        this.isDimmable = isDimmable;
    }


    public Boolean getIsFactory() {
        return isFactory;
    }


    public void setIsFactory(Boolean isFactory) {
        this.isFactory = isFactory;
    }


    public Integer getIsVariableColorTemp() {
        return isVariableColorTemp;
    }


    public void setIsVariableColorTemp(Integer isVariableColorTemp) {
        this.isVariableColorTemp = isVariableColorTemp;
    }


    public Integer getLatitudeI() {
        return latitudeI;
    }


    public void setLatitudeI(Integer latitudeI) {
        this.latitudeI = latitudeI;
    }


    public LightState getLightState() {
        return lightState;
    }


    public void setLightState(LightState lightState) {
        this.lightState = lightState;
    }


    public Integer getLongitudeI() {
        return longitudeI;
    }


    public void setLongitudeI(Integer longitudeI) {
        this.longitudeI = longitudeI;
    }


    public String getMicMac() {
        return micMac;
    }


    public void setMicMac(String micMac) {
        this.micMac = micMac;
    }


    public String getMicType() {
        return micType;
    }


    public void setMicType(String micType) {
        this.micType = micType;
    }


    public String getModel() {
        return model;
    }


    public void setModel(String model) {
        this.model = model;
    }


    public String getOemId() {
        return oemId;
    }


    public void setOemId(String oemId) {
        this.oemId = oemId;
    }


    public Integer getRssi() {
        return rssi;
    }


    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getSwVer() {
        return swVer;
    }


    public void setSwVer(String swVer) {
        this.swVer = swVer;
    }

    public boolean isOn() {
        return lightState.getOnOff() == 1;
    }

    public boolean isTemperatureSupported() {
        return getIsVariableColorTemp() == 1;
    }

    public Integer getMinTemperature() {
        //FIXME harcoded because I don't know how to get
        return 2500;
    }

    public Integer getMaxTemperature() {
        //FIXME harcoded because I don't know how to get
        return 9000;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetSysinfo that = (GetSysinfo) o;
        return Objects.equals(activeMode, that.activeMode) && Objects.equals(alias, that.alias) && Objects.equals(ctrlProtocols, that.ctrlProtocols) && Objects.equals(description, that.description) && Objects.equals(devState, that.devState) && Objects.equals(deviceId, that.deviceId) && Objects.equals(discoVer, that.discoVer) && Objects.equals(errCode, that.errCode) && Objects.equals(hwId, that.hwId) && Objects.equals(hwVer, that.hwVer) && Objects.equals(isColor, that.isColor) && Objects.equals(isDimmable, that.isDimmable) && Objects.equals(isFactory, that.isFactory) && Objects.equals(isVariableColorTemp, that.isVariableColorTemp) && Objects.equals(latitudeI, that.latitudeI) && Objects.equals(lightState, that.lightState) && Objects.equals(longitudeI, that.longitudeI) && Objects.equals(micMac, that.micMac) && Objects.equals(micType, that.micType) && Objects.equals(model, that.model) && Objects.equals(oemId, that.oemId) && Objects.equals(rssi, that.rssi) && Objects.equals(status, that.status) && Objects.equals(swVer, that.swVer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeMode, alias, ctrlProtocols, description, devState, deviceId, discoVer, errCode, hwId, hwVer, isColor, isDimmable, isFactory, isVariableColorTemp, latitudeI, lightState, longitudeI, micMac, micType, model, oemId, rssi, status, swVer);
    }

    @Override
    public String toString() {
        return "GetSysinfo{" +
                "activeMode='" + activeMode + '\'' +
                ", alias='" + alias + '\'' +
                ", ctrlProtocols=" + ctrlProtocols +
                ", description='" + description + '\'' +
                ", devState='" + devState + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", discoVer='" + discoVer + '\'' +
                ", errCode=" + errCode +
                ", hwId='" + hwId + '\'' +
                ", hwVer='" + hwVer + '\'' +
                ", isColor=" + isColor +
                ", isDimmable=" + isDimmable +
                ", isFactory=" + isFactory +
                ", isVariableColorTemp=" + isVariableColorTemp +
                ", latitudeI=" + latitudeI +
                ", lightState=" + lightState +
                ", longitudeI=" + longitudeI +
                ", micMac='" + micMac + '\'' +
                ", micType='" + micType + '\'' +
                ", model='" + model + '\'' +
                ", oemId='" + oemId + '\'' +
                ", rssi=" + rssi +
                ", status='" + status + '\'' +
                ", swVer='" + swVer + '\'' +
                '}';
    }
}
