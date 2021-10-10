package com.demetrio.bulber.engine;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.view.Device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KasaManager {

    private static final BulberProperties props = BulberProperties.getInstance();

    private KasaManager() {}

    public static List<Device> getDevices() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        List<Device> devices = new ArrayList<>();
        Process process = runtime.exec(props.getProperty(BulberConst.COMMAND_DISCOVER));
        process.waitFor();

        if (process.exitValue() == 0) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Pattern pattern = Pattern.compile(props.getProperty(BulberConst.DEVICE_NAME_REGEX));
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        String deviceName = matcher.group(props.getProperty(BulberConst.DEVICE_NAME_PROPERTY));
                        if ((line = reader.readLine()) != null) {
                            pattern = Pattern.compile(props.getProperty(BulberConst.DEVICE_ADDRESS_REGEX));
                            matcher = pattern.matcher(line);
                            if (matcher.matches()) {
                                String address = matcher.group(props.getProperty(BulberConst.DEVICE_ADDRESS_PROPERTY));
                                if ((line = reader.readLine()) != null) {
                                    pattern = Pattern.compile(props.getProperty(BulberConst.DEVICE_STATE_REGEX));
                                    matcher = pattern.matcher(line);
                                    if (matcher.matches()) {
                                        Device device = new Device(deviceName, address, "ON".equals(matcher.group(props.getProperty(BulberConst.DEVICE_STATE_PROPERTY))));
                                        int[] temperatureRange = getTemperatureRange(device);
                                        if (temperatureRange != null) {
                                            device.setMinTemperature(temperatureRange[0]);
                                            device.setMaxTemperature(temperatureRange[1]);
                                            device.setDefaultTemperature(temperatureRange[2] == 0 ? temperatureRange[0] : temperatureRange[2]);
                                        }
                                        devices.add(device);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return devices;
    }

    private static int[] getTemperatureRange(Device device) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(props.getProperty(BulberConst.COMMAND_TEMPERATURE_RANGE).replace("{}", device.getAddress()));
        process.waitFor();

        if (process.exitValue() == 0) {
            int[] rangeAndDefault = new int[3];
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Pattern pattern = Pattern.compile(props.getProperty(BulberConst.DEVICE_TEMPERATURE_REGEX));
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        rangeAndDefault[2] = Integer.parseInt(matcher.group(props.getProperty(BulberConst.DEVICE_TEMPERATURE_PROPERTY)));
                        if ((line = reader.readLine()) != null) {
                            pattern = Pattern.compile(props.getProperty(BulberConst.DEVICE_TEMPERATURE_RANGE_REGEX));
                            matcher = pattern.matcher(line);
                            if (matcher.matches()) {
                                rangeAndDefault[0] = Integer.parseInt(matcher.group(props.getProperty(BulberConst.DEVICE_MIN_TEMPERATURE_PROPERTY)));
                                rangeAndDefault[1] = Integer.parseInt(matcher.group(props.getProperty(BulberConst.DEVICE_MAX_TEMPERATURE_PROPERTY)));
                            }
                        }
                    }
                }
            }
            return rangeAndDefault;
        }

        return null;
    }

    public static String executeCommand(String command) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);

        process.waitFor();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

}
