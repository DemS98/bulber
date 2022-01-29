package com.demetrio.bulber.engine;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.engine.bulb.LightState;
import com.demetrio.bulber.engine.bulb.Request;
import com.demetrio.bulber.view.Device;
import com.demetrio.bulber.view.UIUpdater;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandParser {

    private enum Command {
        COLOR,
        BRIGHTNESS,
        TRANSITION,
        TEMPERATURE,
        RGB,
        ON,
        OFF;

        static Command fromVocal(String vocal) {
            if (vocal.equals(props.getProperty(BulberConst.COMMAND_VOCAL_COLOR))) {
                return Command.COLOR;
            }

            if (vocal.equals(props.getProperty(BulberConst.COMMAND_VOCAL_BRIGHTNESS))) {
                return Command.BRIGHTNESS;
            }

            if (vocal.equals(props.getProperty(BulberConst.COMMAND_VOCAL_TRANSITION))) {
                return Command.TRANSITION;
            }

            if (vocal.equals(props.getProperty(BulberConst.COMMAND_VOCAL_TEMPERATURE))) {
                return Command.TEMPERATURE;
            }

            if (vocal.equals(props.getProperty(BulberConst.COMMAND_VOCAL_RGB))) {
                return Command.RGB;
            }

            if (vocal.equals(props.getProperty(BulberConst.COMMAND_VOCAL_LIGHT_ON))) {
                return Command.ON;
            }

            if (vocal.equals(props.getProperty(BulberConst.COMMAND_VOCAL_LIGHT_OFF))) {
                return Command.OFF;
            }

            return null;
        }
    }

    private static final BulberProperties props = BulberProperties.getInstance();
    private static final CommandParser INSTANCE = new CommandParser();

    private final Map<String, int[]> hsvMap;

    private CommandParser() {
        hsvMap = new HashMap<>();
        initHsvMap();
    }

    public ResultAction parse(Device device, String vocal, UIUpdater updater) {
        boolean commandPresent = false;
        LightState changeState = new LightState();
        String[] words = vocal.split(props.getProperty(BulberConst.RECOGNIZER_COMMAND_REGEX));
        List<Runnable> uiTasks = new ArrayList<>();
        int minTemperature = device.getBulb().getSystem().getGetSysinfo().getMinTemperature();
        int maxTemperature = device.getBulb().getSystem().getGetSysinfo().getMaxTemperature();

        changeState.setOnOff(1);
        for(int i=0; i<words.length; i++) {
            Command command = Command.fromVocal(words[i]);
            if (command != null) {
                switch (command) {
                    case COLOR:
                        if (updater.isDeviceOn() && i + 1 < words.length && !commandPresent) {
                            commandPresent = true;
                            int[] hsvColor = hsvMap.get(words[++i]);
                            if (hsvColor != null) {
                                changeState.setHue(hsvColor[0]);
                                changeState.setSaturation(hsvColor[1]);
                                changeState.setBrightness(hsvColor[2]);
                                changeState.setColorTemp(0);
                                int index = i - 1;
                                uiTasks.add(() -> updater.updateColor(words[index]));
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                        break;
                    case RGB:
                        if (updater.isDeviceOn() && i + 3 < words.length && !commandPresent) {
                            commandPresent = true;
                            try {
                                int red = Integer.parseInt(words[++i]);
                                if (red >= 0 && red <= 255) {
                                    int green = Integer.parseInt(words[++i]);
                                    if (green >= 0 && green <= 255) {
                                        int blue = Integer.parseInt(words[++i]);
                                        if (blue >= 0 && blue <= 255) {
                                            int[] hsv = fromRGBToHSV(red, green, blue);
                                            changeState.setHue(hsv[0]);
                                            changeState.setSaturation(hsv[1]);
                                            changeState.setBrightness(hsv[2]);
                                            changeState.setColorTemp(0);
                                            uiTasks.add(() -> updater.updateColor(red, green, blue));
                                        } else {
                                            return null;
                                        }
                                    } else {
                                        return null;
                                    }
                                } else {
                                    return null;
                                }
                            } catch (NumberFormatException e) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                        break;
                    case BRIGHTNESS:
                        if (updater.isDeviceOn() && i + 1 < words.length && !commandPresent) {
                            commandPresent = true;
                            try {
                                int value = Integer.parseInt(words[++i]);
                                if (value >= props.getIntProperty(BulberConst.BRIGHTNESS_MIN_VALUE) &&
                                        value <= props.getIntProperty(BulberConst.BRIGHTNESS_MAX_VALUE)) {
                                    changeState.setBrightness(value);
                                    uiTasks.add(() -> updater.updateBrightness(value));
                                } else {
                                    return null;
                                }
                            }
                            catch (NumberFormatException e) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                        break;
                    case TRANSITION:
                        if (words.length - (i + 1) <= 2 && commandPresent) {
                            try {
                                int value = Integer.parseInt(words[++i]);
                                if (i + 1 < words.length && props.getStringListProperty(BulberConst.COMMAND_VOCAL_TRANSITION_SECONDS).contains(words[i+1])) {
                                    i++;
                                    value *= 1000;
                                }
                                if (value >= props.getIntProperty(BulberConst.TRANSITION_MIN_VALUE) * 1000 && value <= props.getIntProperty(BulberConst.TRANSITION_MAX_VALUE) * 1000) {
                                    changeState.setTransitionPeriod(value);
                                    int finalValue = value;
                                    uiTasks.add(() -> updater.updateTransition(finalValue));
                                } else {
                                    return null;
                                }
                            }
                            catch (NumberFormatException e) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                        break;
                    case TEMPERATURE:
                        if (updater.isDeviceOn() && updater.isTemperatureSupported() && i + 1 < words.length && !commandPresent) {
                            commandPresent = true;
                            try {
                                int value = Integer.parseInt(words[++i]);
                                if (value >= minTemperature && value <= maxTemperature) {
                                    changeState.setColorTemp(value);
                                    uiTasks.add(() -> updater.updateTemperature(value));
                                } else {
                                    return null;
                                }
                            }
                            catch (NumberFormatException e) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                        break;
                    case ON: case OFF:
                        if (((updater.isDeviceOn() && command == Command.OFF) ||
                                (!updater.isDeviceOn() && command == Command.ON)) && !commandPresent) {
                            commandPresent = true;
                            changeState.setOnOff(command == Command.ON ? 1 : 0);
                            uiTasks.add(updater::switchPower);
                        } else {
                            return null;
                        }
                        break;
                }
            } else {
                return null;
            }
        }
        return new ResultAction(new Request(new Request.Service(changeState)), uiTasks);
    }

    public int[] fromRGBToHSV(int red, int green, int blue) {
        float[] hsv = Color.RGBtoHSB(red, green, blue, new float[3]);
        return new int[] {
                BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(),
                BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(),
                BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()
        };
    }

    public void refresh() {
        hsvMap.clear();
        initHsvMap();
    }

    public static CommandParser getInstance() {
        return INSTANCE;
    }

    private void initHsvMap() {
        if (hsvMap != null && hsvMap.isEmpty()) {
            float[] hsv = new float[3];

            Color color = Color.BLACK;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_BLACK), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.BLUE;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_BLUE), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.MAGENTA;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_MAGENTA), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.CYAN;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_CYAN), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.GRAY;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_GRAY), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.GREEN;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_GREEN), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.ORANGE;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_ORANGE), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.PINK;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_PINK), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.RED;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_RED), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.WHITE;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_WHITE), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;

            color = Color.YELLOW;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_YELLOW), new int[] {BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue(), BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue()});;
        }
    }

}
