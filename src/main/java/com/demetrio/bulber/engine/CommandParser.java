package com.demetrio.bulber.engine;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.view.Device;
import com.demetrio.bulber.view.UIUpdater;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandParser {

    public enum Command {
        COLOR(props.getProperty(BulberConst.COMMAND_COLOR)),
        BRIGHTNESS(props.getProperty(BulberConst.COMMAND_BRIGHTNESS)),
        TRANSITION(props.getProperty(BulberConst.COMMAND_TRANSITION)),
        TEMPERATURE(props.getProperty(BulberConst.COMMAND_TEMPERATURE)),
        RGB(props.getProperty(BulberConst.COMMAND_COLOR)),
        ON(props.getProperty(BulberConst.COMMAND_LIGHT_ON)),
        OFF(props.getProperty(BulberConst.COMMAND_LIGHT_OFF));

        private final String kasaCommand;

        Command(String kasaCommand) {
            this.kasaCommand = kasaCommand;
        }

        String getKasaCommand() {
            return kasaCommand;
        }

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

    private final String baseKasaCommand;
    private final Map<String, String> hsvMap;

    private CommandParser() {
        hsvMap = new HashMap<>();
        initHsvMap();

        baseKasaCommand = props.getProperty(BulberConst.COMMAND_KASA);
    }

    public ResultAction parse(Device device, String vocal, UIUpdater updater) {
        StringBuilder sb = new StringBuilder(baseKasaCommand.replace("{}", device.getAddress()));
        boolean commandPresent = false;
        String[] words = vocal.split(props.getProperty(BulberConst.RECOGNIZER_COMMAND_REGEX));
        List<Runnable> uiTasks = new ArrayList<>();
        for(int i=0; i<words.length; i++) {
            Command command = Command.fromVocal(words[i]);
            if (command != null) {
                sb.append(' ');
                switch (command) {
                    case COLOR:
                        if (updater.isDeviceOn() && i + 1 < words.length && !commandPresent) {
                            commandPresent = true;
                            String hsvColor = hsvMap.get(words[++i]);
                            if (hsvColor != null) {
                                sb.append(command.getKasaCommand());
                                sb.append(' ');
                                sb.append(hsvColor);
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
                                            float[] hsv = Color.RGBtoHSB(red, green, blue, null);
                                            sb.append(command.getKasaCommand());
                                            sb.append(' ');
                                            sb.append(BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP)).append(' ').append(BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP)).append(' ').append(BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));
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
                                    sb.append(command.getKasaCommand());
                                    sb.append(' ');
                                    sb.append(value);
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
                        if (updater.isDeviceOn() && words.length - (i + 1) <= 2 && sb.indexOf(command.getKasaCommand()) == -1) {
                            try {
                                int value = Integer.parseInt(words[++i]);
                                if (i + 1 < words.length && words[i+1].equals(props.getProperty(BulberConst.COMMAND_VOCAL_TRANSITION_SECONDS))) {
                                    i++;
                                    value *= 1000;
                                }
                                if (value >= props.getIntProperty(BulberConst.TRANSITION_MIN_VALUE) * 1000 && value <= props.getIntProperty(BulberConst.TRANSITION_MAX_VALUE) * 1000) {
                                    sb.append(command.getKasaCommand());
                                    sb.append(' ');
                                    sb.append(value);
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
                                if (value >= device.getMinTemperature() && value <= device.getMaxTemperature()) {
                                    sb.append(command.getKasaCommand());
                                    sb.append(' ');
                                    sb.append(value);
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
                                (!updater.isDeviceOn() && command == Command.ON)) && words.length == 1) {
                            sb.append(command.getKasaCommand());
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
        return new ResultAction(sb.toString(), uiTasks);
    }

    public String parse(Device device, Map<Command, List<Object>> commandMap) {
        StringBuilder sb = new StringBuilder(baseKasaCommand.replace("{}", device.getAddress()));
        boolean commandPresent = false;
        for(Map.Entry<Command, List<Object>> entry : commandMap.entrySet()) {
            Command command = entry.getKey();
            List<Object> params = entry.getValue();
            sb.append(' ');
            switch (command) {
                case RGB:
                    if (params.size() == 3 && !commandPresent) {
                        commandPresent = true;
                        try {
                            int red = (Integer) params.get(0);
                            if (red >= 0 && red <= 255) {
                                int green = (Integer) params.get(1);
                                if (green >= 0 && green <= 255) {
                                    int blue = (Integer) params.get(2);
                                    if (blue >= 0 && blue <= 255) {
                                        float[] hsv = Color.RGBtoHSB(red, green, blue, null);
                                        sb.append(command.getKasaCommand());
                                        sb.append(' ');
                                        sb.append(BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP)).append(' ').append(BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP)).append(' ').append(BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));
                                    } else {
                                        return null;
                                    }
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        } catch (ClassCastException e) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    break;
                case BRIGHTNESS:
                    if (params.size() == 1 && !commandPresent) {
                        commandPresent = true;
                        try {
                            int value = (Integer) params.get(0);
                            if (value >= props.getIntProperty(BulberConst.BRIGHTNESS_MIN_VALUE) && value <=
                                    props.getIntProperty(BulberConst.BRIGHTNESS_MAX_VALUE)) {
                                sb.append(command.getKasaCommand());
                                sb.append(' ');
                                sb.append(value);
                            } else {
                                return null;
                            }
                        }
                        catch (ClassCastException e) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    break;
                case TRANSITION:
                    if (params.size() == 1 && sb.indexOf(command.getKasaCommand()) == -1) {
                        try {
                            int value = (Integer) params.get(0);
                            if (value >= props.getIntProperty(BulberConst.TRANSITION_MIN_VALUE) * 1000 &&
                                    value <= props.getIntProperty(BulberConst.TRANSITION_MAX_VALUE) * 1000){
                                sb.append(command.getKasaCommand());
                                sb.append(' ');
                                sb.append(value);
                            } else {
                                return null;
                            }
                        }
                        catch (ClassCastException e) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    break;
                case TEMPERATURE:
                    if (params.size() == 1 && !commandPresent) {
                        commandPresent = true;
                        try {
                            int value = (Integer) params.get(0);
                            if (value >= device.getMinTemperature() && value <= device.getMaxTemperature()) {
                                sb.append(command.getKasaCommand());
                                sb.append(' ');
                                sb.append(value);
                            } else {
                                return null;
                            }
                        }
                        catch (ClassCastException e) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    break;
                case ON: case OFF:
                    if (params.isEmpty()) {
                        sb.append(command.getKasaCommand());
                    } else {
                        return null;
                    }
                    break;
            }
        }
        return sb.toString();
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
            hsvMap.put(props.getProperty(BulberConst.COLOR_BLACK), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.BLUE;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_BLUE), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.MAGENTA;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_MAGENTA), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.CYAN;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_CYAN), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.GRAY;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_GRAY), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.GREEN;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_GREEN), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.ORANGE;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_ORANGE), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.PINK;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_PINK), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.RED;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_RED), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.WHITE;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_WHITE), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));

            color = Color.YELLOW;
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
            hsvMap.put(props.getProperty(BulberConst.COLOR_YELLOW), "" + BigDecimal.valueOf(hsv[0]).multiply(new BigDecimal("360")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[1]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + ' ' + BigDecimal.valueOf(hsv[2]).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP));
        }
    }

    private Color getColorFromName(String col) {
        Color color = null;
        switch (col.toLowerCase()) {
            case "black":
                color = Color.BLACK;
                break;
            case "blue":
                color = Color.BLUE;
                break;
            case "cyan":
                color = Color.CYAN;
                break;
            case "gray":
                color = Color.GRAY;
                break;
            case "green":
                color = Color.GREEN;
                break;
            case "yellow":
                color = Color.YELLOW;
                break;
            case "magneta":
                color = Color.MAGENTA;
                break;
            case "orange":
                color = Color.ORANGE;
                break;
            case "pink":
                color = Color.PINK;
                break;
            case "red":
                color = Color.RED;
                break;
            case "white":
                color = Color.WHITE;
                break;
        }
        return color;
    }
}
