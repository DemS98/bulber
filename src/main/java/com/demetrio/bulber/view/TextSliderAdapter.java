package com.demetrio.bulber.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSliderAdapter implements MouseMotionListener, KeyListener, FocusListener {

    private static final Logger logger = LoggerFactory.getLogger(TextSliderAdapter.class);

    private int prevValue;
    private final JTextComponent textComponent;
    private String measure;
    private final JSlider slider;
    private boolean constant;

    public TextSliderAdapter(JSlider slider, JTextComponent textComponent, String measure) {
        prevValue = -1;
        this.textComponent = textComponent;
        this.slider = slider;
        textComponent.addKeyListener(this);
        textComponent.addFocusListener(this);
        this.measure = measure;
        this.constant = false;
    }

    public TextSliderAdapter(JSlider slider, JTextComponent textComponent, String measure, boolean constant) {
        prevValue = -1;
        this.textComponent = textComponent;
        this.slider = slider;
        textComponent.addKeyListener(this);
        textComponent.addFocusListener(this);
        this.measure = measure;
        this.constant = constant;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        JSlider slider = (JSlider) e.getComponent();
        int value = slider.getValue();
        if (prevValue != value) {
            textComponent.setText(value + measure);
            if (constant && value % slider.getMajorTickSpacing() > 0) {
                slider.setValue(value + slider.getMajorTickSpacing());
            }
            prevValue = value;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        doTextComponentAction((JTextComponent) e.getComponent());
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            doTextComponentAction((JTextComponent) e.getComponent());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    private void doTextComponentAction(JTextComponent component) {
        Pattern pattern = Pattern.compile("^(?<number>\\d+)(?<measure>\\w*)$");
        Matcher matcher = pattern.matcher(component.getText());
        if (matcher.matches()) {
            String measure = matcher.group("measure");
            if (measure.isEmpty() || measure.equals(this.measure)) {
                try {
                    int number = Integer.parseInt(matcher.group("number"));
                    if (number >= slider.getMinimum() && number <= slider.getMaximum()) {
                        slider.setValue(number);
                        if (measure.isEmpty())
                            component.setText(component.getText() + this.measure);
                    }
                } catch (NumberFormatException ex) {
                    logger.error("Error parsing textField number", ex);
                }
            }
        }
    }
}
