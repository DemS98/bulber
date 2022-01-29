package com.demetrio.bulber.view;

import com.demetrio.bulber.engine.CommandParser;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class UIUpdater {
    private JColorChooser colorChooser;
    private JSlider brightness;
    private JTextField brightnessTextField;
    private JSlider transition;
    private JTextField transitionTextField;
    private JToggleButton onOff;
    private Consumer<Boolean> onOffAction;
    private JSlider temperature;
    private JTextField temperatureTextField;

    public static class Builder {
        private JColorChooser colorChooser;
        private JSlider brightness;
        private JTextField brightnessTextField;
        private JSlider transition;
        private JTextField transitionTextField;
        private JToggleButton onOff;
        private Consumer<Boolean> onOffAction;
        private JSlider temperature;
        private JTextField temperatureTextField;

        private Builder() {

        }

        public Builder withColorPicker(JColorChooser colorPicker) {
            this.colorChooser = colorPicker;
            return this;
        }

        public Builder withBrightness(JSlider brightness, JTextField label) {
            this.brightness = brightness;
            this.brightnessTextField = label;
            return this;
        }

        public Builder withTransition(JSlider transition, JTextField label) {
            this.transition = transition;
            this.transitionTextField = label;
            return  this;
        }

        public Builder withPowerButton(JToggleButton powerButton, Consumer<Boolean> action) {
            this.onOff = powerButton;
            this.onOffAction = action;
            return this;
        }

        public Builder withTemperature(JSlider temperature, JTextField label) {
            this.temperature = temperature;
            this.temperatureTextField = label;
            return this;
        }

        public UIUpdater build() {
            return new UIUpdater(colorChooser, brightness, brightnessTextField, transition, transitionTextField, onOff,
                    onOffAction, temperature, temperatureTextField);
        }
    }

    private UIUpdater(JColorChooser colorChooser, JSlider brightness, JTextField brightnessTextField, JSlider transition,
                      JTextField transitionTextField, JToggleButton onOff, Consumer<Boolean> onOffAction, JSlider temperature, JTextField temperatureTextField) {
        this.colorChooser = colorChooser;
        this.brightness = brightness;
        this.brightnessTextField = brightnessTextField;
        this.transition = transition;
        this.transitionTextField = transitionTextField;
        this.onOff = onOff;
        this.onOffAction = onOffAction;
        this.temperature = temperature;
        this.temperatureTextField = temperatureTextField;
    }

    public void updateColor(String color) {
        colorChooser.setColor(getColorFromName(color));
    }

    public void updateColor(int red, int green, int blue) {
        colorChooser.setColor(red, green, blue);
    }

    public void updateBrightness(int brightness) {
        this.brightness.setValue(brightness);
        brightnessTextField.setText(brightness + "%");
    }

    public void updateTransition(int transition) {
        if (transitionTextField.getText().endsWith("ms")) {
            this.transition.setValue(transition);
            transitionTextField.setText(transition + "ms");
        } else {
            this.transition.setValue(transition /= 1000);
            transitionTextField.setText(transition + "s");
        }
    }

    public void switchPower() {
        boolean selected = !isDeviceOn();
        onOff.setSelected(selected);
        onOffAction.accept(selected);
    }

    public void updateTemperature(int temperature) {
        if (this.temperature != null) {
            this.temperature.setValue(temperature);
            temperatureTextField.setText(temperature + "K");
        }
    }

    public boolean isDeviceOn() {
        return onOff.isSelected();
    }

    public boolean isTemperatureSupported() {
        return temperature != null;
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

    public static Builder builder() {
        return new Builder();
    }
}
