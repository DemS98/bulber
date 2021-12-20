package com.demetrio.bulber.view;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.engine.CommandParser;
import com.demetrio.bulber.engine.KasaManager;
import com.demetrio.bulber.engine.MicrophoneRecognition;
import com.demetrio.bulber.engine.VocalNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.demetrio.bulber.engine.CommandParser.Command;

public class ControlPanel extends JPanel implements Closeable {

    private static final BulberProperties props = BulberProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ControlPanel.class);

    private final MicrophoneRecognition mr;
    private JPanel temperaturePanel;
    private JSlider temperature;
    private JButton confirmTemperature;
    private JTextField temperatureTextField;
    private final JSlider transition;
    private final JRadioButton seconds;
    private Timer slideText;

    public ControlPanel(Device device) throws LineUnavailableException {
        super();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new BorderLayout());

        JButton backButton = new JButton(new ImageIcon(new ImageIcon(Objects.requireNonNull(ControlPanel.class.getClassLoader()
                .getResource(props.getProperty(BulberConst.BACK_PATH)))).getImage()
                .getScaledInstance(props.getIntProperty(BulberConst.BACK_SIZE), props.getIntProperty(BulberConst.BACK_SIZE), Image.SCALE_SMOOTH)));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);

        JPanel micDisplayPanel = new JPanel();
        micDisplayPanel.setLayout(new BoxLayout(micDisplayPanel, BoxLayout.Y_AXIS));
        micDisplayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true),
                props.getProperty(BulberConst.MICROPHONE_BORDER_TITLE)));

        JPanel micPanel = new JPanel();

        JButton micButton = new JButton(new ImageIcon(new ImageIcon(Objects.requireNonNull(ControlPanel.class.getClassLoader()
                .getResource(props.getProperty(BulberConst.MIC_ICON_PATH)))).getImage()
                .getScaledInstance(props.getIntProperty(BulberConst.MIC_WIDTH), props.getIntProperty(BulberConst.MIC_HEIGHT), Image.SCALE_SMOOTH)));
        micButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        micButton.setBorderPainted(false);
        micButton.setFocusPainted(false);
        micButton.setContentAreaFilled(false);
        micButton.setPressedIcon(new ImageIcon(new ImageIcon(Objects.requireNonNull(ControlPanel.class.getClassLoader()
                .getResource(props.getProperty(BulberConst.MIC_ON_ICON_PATH)))).getImage()
                .getScaledInstance(props.getIntProperty(BulberConst.MIC_WIDTH), props.getIntProperty(BulberConst.MIC_HEIGHT), Image.SCALE_SMOOTH)));
        micButton.setDisabledIcon(new ImageIcon(new ImageIcon(Objects.requireNonNull(ControlPanel.class.getClassLoader()
                .getResource(props.getProperty(BulberConst.MIC_OFF_ICON_PATH)))).getImage()
                .getScaledInstance(props.getIntProperty(BulberConst.MIC_WIDTH), props.getIntProperty(BulberConst.MIC_HEIGHT), Image.SCALE_SMOOTH)));
        JCheckBox micCheckBox = new JCheckBox(props.getProperty(BulberConst.MICROPHONE_ON_ALWAYS));

        micPanel.add(micButton);
        micPanel.add(micCheckBox);

        JPanel displayPanel = new JPanel();
        LCDisplay display = new LCDisplay();
        display.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10, false));
        display.setEnabled(device.isOn());
        displayPanel.add(display);

        int lcdisplayMaxVelocity = props.getIntProperty(BulberConst.LCDISPLAY_MAX_VELOCITY);
        int lcdisplayMinVelocity = props.getIntProperty(BulberConst.LCDISPLAY_MIN_VELOCITY);
        JSlider displayVelocity = new JSlider(lcdisplayMaxVelocity, lcdisplayMinVelocity, props.getIntProperty(BulberConst.LCDISPLAY_DEFAULT_VELOCITY));
        displayVelocity.setEnabled(device.isOn());

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(lcdisplayMaxVelocity, new JLabel(props.getProperty(BulberConst.LCDISPLAY_MAX_VELOCITY_LABEL)));
        labels.put(lcdisplayMinVelocity, new JLabel(props.getProperty(BulberConst.LCDISPLAY_MIN_VELOCITY_LABEL)));
        displayVelocity.setLabelTable(labels);
        displayVelocity.setPaintLabels(true);

        micDisplayPanel.add(micPanel);
        micDisplayPanel.add(Box.createVerticalStrut(10));
        micDisplayPanel.add(displayPanel);
        micDisplayPanel.add(Box.createVerticalStrut(10));
        micDisplayPanel.add(displayVelocity);

        String confirmButtonText = props.getProperty(BulberConst.BUTTON_CONFIRM);

        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
        JScrollPane commandScrollPane = new JScrollPane(commandPanel);
        commandScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.COMMAND_PANEL_BORDER_TITLE)));


        JPanel onOffPanel = new JPanel();
        ImageIcon on = new ImageIcon(new ImageIcon(Objects.requireNonNull(ControlPanel.class.getClassLoader()
                .getResource(props.getProperty(BulberConst.BULB_ON)))).getImage()
                .getScaledInstance(props.getIntProperty(BulberConst.BULB_WIDTH), props.getIntProperty(BulberConst.BULB_HEIGHT), Image.SCALE_SMOOTH));
        ImageIcon off = new ImageIcon(new ImageIcon(Objects.requireNonNull(ControlPanel.class.getClassLoader()
                .getResource(props.getProperty(BulberConst.BULB_OFF)))).getImage()
                .getScaledInstance(props.getIntProperty(BulberConst.BULB_WIDTH), props.getIntProperty(BulberConst.BULB_HEIGHT), Image.SCALE_SMOOTH));
        JToggleButton onOff = new JToggleButton();
        onOff.setCursor(new Cursor(Cursor.HAND_CURSOR));
        onOff.setBorderPainted(false);
        onOff.setFocusPainted(false);
        onOff.setContentAreaFilled(false);
        onOff.setSelectedIcon(on);
        onOff.setIcon(off);
        onOff.setSelected(device.isOn());
        onOffPanel.add(onOff);

        JPanel colorPickerPanel = new JPanel();
        colorPickerPanel.setLayout(new BoxLayout(colorPickerPanel, BoxLayout.Y_AXIS));
        colorPickerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.COLOR_PICKER_BORDER_TITLE)));
        JColorChooser colorPicker = new JColorChooser();
        colorPicker.setPreferredSize(new Dimension(colorPicker.getWidth(), 220));
        colorPicker.setPreviewPanel(new JPanel());
        for (AbstractColorChooserPanel accp : colorPicker.getChooserPanels()) {
            String name = accp.getDisplayName();
            if (name.equals("HSV") || name.equals("HSL") || name.equals("CMYK")) {
                colorPicker.removeChooserPanel(accp);
            }
        }
        colorPicker.setEnabled(device.isOn());
        JPanel confirmColorPanel = new JPanel();
        JButton confirmColor = new JButton(confirmButtonText);
        confirmColor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmColorPanel.add(confirmColor);
        colorPickerPanel.add(colorPicker);
        colorPickerPanel.add(Box.createVerticalStrut(10));
        colorPickerPanel.add(confirmColorPanel);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(2, 1, 0, 10));

        JPanel brightnessTemperaturePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JPanel brightnessPanel = new JPanel();
        brightnessPanel.setLayout(new BoxLayout(brightnessPanel, BoxLayout.Y_AXIS));
        brightnessPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.BRIGHTNESS_BORDER_TITLE)));
        int brightnessMinValue = props.getIntProperty(BulberConst.BRIGHTNESS_MIN_VALUE);
        int brightnessDefaultValue = props.getIntProperty(BulberConst.BRIGHTNESS_DEFAULT_VALUE);
        int brightnessMaxValue = props.getIntProperty(BulberConst.BRIGHTNESS_MAX_VALUE);
        JPanel brightnessTextFieldPanel = new JPanel();
        JTextField brightnessTextField = new JTextField(brightnessDefaultValue + "%", 4);
        brightnessTextField.setHorizontalAlignment(SwingConstants.CENTER);
        brightnessTextFieldPanel.add(brightnessTextField);
        JSlider brightness = new JSlider(brightnessMinValue, brightnessMaxValue, brightnessDefaultValue);
        brightness.setPaintTicks(true);
        brightness.setMajorTickSpacing(10);
        Hashtable<Integer, JLabel> brightnessLabels = new Hashtable<>();
        brightnessLabels.put(brightnessMinValue, new JLabel(brightnessMinValue + "%"));
        brightnessLabels.put(brightnessMaxValue, new JLabel(brightnessMaxValue + "%"));
        brightness.setLabelTable(brightnessLabels);
        brightness.setPaintLabels(true);
        brightness.setEnabled(device.isOn());
        JPanel confirmBrightnessPanel = new JPanel();
        JButton confirmBrightness = new JButton(confirmButtonText);
        confirmBrightness.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBrightnessPanel.add(confirmBrightness);
        brightnessPanel.add(brightnessTextFieldPanel);
        brightnessPanel.add(Box.createVerticalStrut(10));
        brightnessPanel.add(brightness);
        brightnessPanel.add(Box.createVerticalStrut(10));
        brightnessPanel.add(confirmBrightnessPanel);
        brightnessTemperaturePanel.add(brightnessPanel);
        if (device.getMinTemperature() > 0 && device.getMaxTemperature() > device.getMinTemperature()) {
            temperaturePanel = new JPanel();
            temperaturePanel.setLayout(new BoxLayout(temperaturePanel, BoxLayout.Y_AXIS));
            temperaturePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.TEMPERATURE_BORDER_TITLE)));
            JPanel temperatureTextFieldPanel = new JPanel();
            temperatureTextField = new JTextField(device.getDefaultTemperature() + "K", String.valueOf(device.getMinTemperature()).length() + 1);
            temperatureTextField.setHorizontalAlignment(SwingConstants.CENTER);
            temperatureTextFieldPanel.add(temperatureTextField);
            temperature = new JSlider(device.getMinTemperature(), device.getMaxTemperature(), device.getDefaultTemperature());
            temperature.setPaintTicks(true);
            temperature.setMajorTickSpacing(500);
            Hashtable<Integer, JLabel> temperatureLabels = new Hashtable<>();
            temperatureLabels.put(device.getMinTemperature(), new JLabel(device.getMinTemperature() + "K"));
            temperatureLabels.put(device.getMaxTemperature(), new JLabel(device.getMaxTemperature() + "K"));
            temperature.setLabelTable(temperatureLabels);
            temperature.setPaintLabels(true);
            temperature.setEnabled(device.isOn());
            JPanel confirmTemperaturePanel = new JPanel();
            confirmTemperature = new JButton(confirmButtonText);
            confirmTemperature.setCursor(new Cursor(Cursor.HAND_CURSOR));
            confirmTemperaturePanel.add(confirmTemperature);
            temperaturePanel.add(temperatureTextFieldPanel);
            temperaturePanel.add(Box.createVerticalStrut(10));
            temperaturePanel.add(temperature);
            temperaturePanel.add(Box.createVerticalStrut(10));
            temperaturePanel.add(confirmTemperaturePanel);
            brightnessTemperaturePanel.add(temperaturePanel);
        }

        JPanel transitionPanel = new JPanel();
        transitionPanel.setLayout(new BoxLayout(transitionPanel, BoxLayout.Y_AXIS));
        transitionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.TRANSITION_BORDER_TITLE)));
        int transitionMinValue = props.getIntProperty(BulberConst.TRANSITION_MIN_VALUE);
        int transitionMaxValueSeconds = props.getIntProperty(BulberConst.TRANSITION_MAX_VALUE);
        JPanel transitionTextFieldPanel = new JPanel();
        JTextField transitionTextField = new JTextField(transitionMinValue + "s", String.valueOf(transitionMaxValueSeconds * 1000).length() + 2);
        transitionTextField.setHorizontalAlignment(SwingConstants.CENTER);
        transitionTextFieldPanel.add(transitionTextField);
        transition = new JSlider(transitionMinValue, transitionMaxValueSeconds, transitionMinValue);
        transition.setPaintTicks(true);
        transition.setMajorTickSpacing(1);
        Hashtable<Integer, JLabel> transitionLabels = new Hashtable<>();
        for(int i=transitionMinValue; i <= transitionMaxValueSeconds; i+=5) {
            transitionLabels.put(i, new JLabel(i + "s"));
        }
        transition.setLabelTable(transitionLabels);
        transition.setPaintLabels(true);
        transition.setEnabled(device.isOn());
        JPanel secMsPanel = new JPanel();
        ButtonGroup secMs = new ButtonGroup();
        seconds = new JRadioButton(props.getProperty(BulberConst.TRANSITION_SECONDS), true);
        seconds.setEnabled(device.isOn());
        JRadioButton milliseconds = new JRadioButton(props.getProperty(BulberConst.TRANSITION_MILLISECONDS));
        milliseconds.setEnabled(device.isOn());
        secMs.add(seconds);
        secMs.add(milliseconds);
        secMsPanel.add(seconds);
        secMsPanel.add(milliseconds);
        transitionPanel.add(transitionTextFieldPanel);
        transitionPanel.add(Box.createVerticalStrut(10));
        transitionPanel.add(transition);
        transitionPanel.add(Box.createVerticalStrut(10));
        transitionPanel.add(secMsPanel);

        optionsPanel.add(brightnessTemperaturePanel);
        optionsPanel.add(transitionPanel);

        commandPanel.add(onOffPanel);
        commandPanel.add(colorPickerPanel);
        commandPanel.add(Box.createVerticalStrut(10));
        commandPanel.add(optionsPanel);

        UIUpdater uiUpdater = UIUpdater.builder()
                .withColorPicker(colorPicker)
                .withBrightness(brightness, brightnessTextField)
                .withTemperature(temperature, temperatureTextField)
                .withTransition(transition, transitionTextField)
                .withPowerButton(onOff)
                .build();

        mr = new MicrophoneRecognition(device, uiUpdater, text -> {
            if (text.length() > 0) {
                if (slideText != null && slideText.isRunning()) {
                    slideText.stop();
                }

                int columns = display.getColumns();

                display.setText(String.join("", Collections.nCopies(columns, " ")));

                StringBuilder sb = new StringBuilder(text);
                for(int i=0; i < columns; i++) {sb.append(' ');}

                AtomicInteger supIndex = new AtomicInteger(0);
                slideText = new Timer(displayVelocity.getValue(), e -> {
                    try {
                        display.setText(display.getText(1, columns - 1));
                    } catch (BadLocationException ex) {
                        logger.error(props.getProperty(BulberConst.UNEXPECTED_ERROR), ex);
                    }
                    display.append(String.valueOf(sb.charAt(supIndex.getAndIncrement())));
                    supIndex.compareAndSet(sb.length(), 0);
                });
                slideText.start();
            }

        });

        int circleSpinSize = props.getIntProperty(BulberConst.CIRCLE_SPIN_DISCOVER_SIZE);
        JPanel flowLanguagePanel = new JPanel();
        Component invisible = Box.createRigidArea(new Dimension(circleSpinSize, circleSpinSize));
        flowLanguagePanel.add(invisible);
        ButtonGroup group = new ButtonGroup();
        props.getSupportedLanguages().forEach(lang -> {
            JRadioButton button = new JRadioButton(new ImageIcon(new ImageIcon(Objects.requireNonNull(Device.class.getClassLoader()
                    .getResource(props.getProperty(BulberConst.LANG_FLAG_PATH).replace("{}", lang)))).getImage()
                    .getScaledInstance(props.getIntProperty(BulberConst.LANG_FLAG_WIDTH), props.getIntProperty(BulberConst.LANG_FLAG_HEIGHT), Image.SCALE_SMOOTH)));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (lang.equals(props.getProperty(BulberConst.LANG))) {
                button.setSelected(true);
                button.setBackground(Color.DARK_GRAY);
            }
            group.add(button);
            flowLanguagePanel.add(button);
            button.addItemListener(actionEvent -> {
                AbstractButton clicked = (AbstractButton) actionEvent.getItem();
                boolean selected = actionEvent.getStateChange() == ItemEvent.SELECTED;

                clicked.setBackground(selected ? Color.DARK_GRAY : null);

                if (selected) {
                    CircleSpin circleSpin = new CircleSpin(circleSpinSize);
                    flowLanguagePanel.add(circleSpin, 0);
                    flowLanguagePanel.remove(invisible);
                    flowLanguagePanel.revalidate();
                    flowLanguagePanel.repaint();
                    Timer timer = new Timer(props.getIntProperty(BulberConst.CIRCLE_SPIN_DELAY), ae -> circleSpin.repaint());
                    timer.start();
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            micCheckBox.setSelected(false);
                            props.changeLanguage(lang);
                            mr.refresh();
                            CommandParser.getInstance().refresh();
                            micDisplayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true),
                                    props.getProperty(BulberConst.MICROPHONE_BORDER_TITLE)));
                            micCheckBox.setText(props.getProperty(BulberConst.MICROPHONE_ON_ALWAYS));
                            Hashtable<Integer, JLabel> newLabels = new Hashtable<>();
                            newLabels.put(lcdisplayMaxVelocity, new JLabel(props.getProperty(BulberConst.LCDISPLAY_MAX_VELOCITY_LABEL)));
                            newLabels.put(lcdisplayMinVelocity, new JLabel(props.getProperty(BulberConst.LCDISPLAY_MIN_VELOCITY_LABEL)));
                            displayVelocity.setLabelTable(newLabels);
                            micDisplayPanel.revalidate();
                            micDisplayPanel.repaint();
                            commandScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.COMMAND_PANEL_BORDER_TITLE)));
                            colorPickerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.COLOR_PICKER_BORDER_TITLE)));
                            confirmColor.setText(props.getProperty(BulberConst.BUTTON_CONFIRM));
                            brightnessPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.BRIGHTNESS_BORDER_TITLE)));
                            confirmBrightness.setText(props.getProperty(BulberConst.BUTTON_CONFIRM));
                            if (temperature != null) {
                                temperaturePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.TEMPERATURE_BORDER_TITLE)));
                                confirmTemperature.setText(props.getProperty(BulberConst.BUTTON_CONFIRM));
                            }
                            transitionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), props.getProperty(BulberConst.TRANSITION_BORDER_TITLE)));
                            seconds.setText(props.getProperty(BulberConst.TRANSITION_SECONDS));
                            milliseconds.setText(props.getProperty(BulberConst.TRANSITION_MILLISECONDS));
                            commandPanel.revalidate();
                            commandPanel.repaint();
                            VocalNumber.getInstance().initCache();
                            return null;
                        }

                        @Override
                        protected void done() {
                            logger.debug("Lang changed: {}", lang);
                            timer.stop();
                            flowLanguagePanel.add(invisible, 0);
                            flowLanguagePanel.remove(circleSpin);
                            flowLanguagePanel.revalidate();
                            flowLanguagePanel.repaint();
                        }
                    }.execute();
                }
            });
        });

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(flowLanguagePanel, BorderLayout.EAST);

        this.add(topPanel);
        this.add(micDisplayPanel);
        this.add(Box.createVerticalStrut(10));
        this.add(commandScrollPane);

        micButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (((JButton) e.getSource()).isEnabled() && !micCheckBox.isSelected()) {
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            mr.run();
                            return null;
                        }
                    }.execute();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (((JButton) e.getSource()).isEnabled()) {
                    mr.stop();
                }
            }

        });

        micCheckBox.addItemListener(itemEvent -> {
            boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
            if (selected) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        mr.run();
                        return null;
                    }
                }.execute();
            } else {
                mr.stop();
            }
            micButton.setEnabled(!selected);
            colorPicker.setEnabled(!selected);
            confirmColor.setEnabled(!selected);
            brightness.setEnabled(!selected);
            brightnessTextField.setEnabled(!selected);
            confirmBrightness.setEnabled(!selected);
            transition.setEnabled(!selected);
            transitionTextField.setEnabled(!selected);
            seconds.setEnabled(!selected);
            milliseconds.setEnabled(!selected);
            if (temperature != null) {
                temperature.setEnabled(!selected);
                temperatureTextField.setEnabled(!selected);
                confirmTemperature.setEnabled(!selected);
            }
        });

        displayVelocity.addChangeListener(changeEvent -> {
            int value = ((JSlider) changeEvent.getSource()).getValue();
            if (slideText != null && slideText.isRunning()) {
                slideText.setDelay(value);
            }
        });

        onOff.addItemListener(itemEvent -> {
            boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {
                    String command = CommandParser.getInstance().parse(device, Collections.singletonMap(selected ? Command.ON : Command.OFF, Collections.emptyList()));
                    logger.debug(props.getProperty(BulberConst.EXECUTE_KASA_COMMAND), command);
                    try {
                        logger.debug(props.getProperty(BulberConst.KASA_COMMAND_RESPONSE), KasaManager.executeCommand(command));
                    } catch (IOException | InterruptedException e) {
                        logger.error(props.getProperty(BulberConst.KASA_COMMAND_ERROR), e);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    colorPicker.setEnabled(selected);
                    confirmColor.setEnabled(selected);
                    brightness.setEnabled(selected);
                    brightnessTextField.setEnabled(selected);
                    confirmBrightness.setEnabled(selected);
                    transition.setEnabled(selected);
                    transitionTextField.setEnabled(selected);
                    seconds.setEnabled(selected);
                    milliseconds.setEnabled(selected);
                    if (temperature != null) {
                        temperature.setEnabled(selected);
                        temperatureTextField.setEnabled(selected);
                        confirmTemperature.setEnabled(selected);
                    }
                }
            }.execute();
        });

        brightness.addMouseMotionListener(new TextSliderAdapter(brightness, brightnessTextField, "%"));

        TextSliderAdapter transitionAdapter = new TextSliderAdapter(transition, transitionTextField, "s", true);
        transition.addMouseMotionListener(transitionAdapter);

        milliseconds.addActionListener(actionEvent -> {
            JRadioButton ms = (JRadioButton) actionEvent.getSource();
            if (ms.isSelected()) {
                transition.setMinimum(transition.getMinimum() * 1000);
                transition.setMaximum(transition.getMaximum() * 1000);
                transition.setValue(transition.getValue() * 1000);
                transition.setMajorTickSpacing(100);
                transition.setSnapToTicks(false);
                transitionAdapter.setMeasure("ms");
                transitionAdapter.setConstant(false);
                transitionTextField.setText(transition.getValue() + "ms");
                Hashtable<Integer, JLabel> newLabels = new Hashtable<>();
                newLabels.put(transition.getMinimum(), new JLabel(transition.getMinimum() + "ms"));
                newLabels.put(transition.getMaximum(), new JLabel(transition.getMaximum() + "ms"));
                transition.setLabelTable(newLabels);
            }
        });

        seconds.addActionListener(actionEvent -> {
            JRadioButton sec = (JRadioButton) actionEvent.getSource();
            if (sec.isSelected()) {
                transition.setValue(transition.getValue() / 1000);
                transition.setMinimum(transition.getMinimum() / 1000);
                transition.setMaximum(transition.getMaximum() / 1000);
                transition.setMajorTickSpacing(1);
                transition.setSnapToTicks(true);
                transitionAdapter.setMeasure("s");
                transitionAdapter.setConstant(true);
                transitionTextField.setText(transition.getValue() + "s");
                Hashtable<Integer, JLabel> newLabels = new Hashtable<>();
                for(int i=transition.getMinimum(); i <= transition.getMaximum(); i+=5) {
                    newLabels.put(i, new JLabel(i + "s"));
                }
                transition.setLabelTable(newLabels);
            }
        });

        confirmColor.addActionListener(actionEvent -> {
            Color color = colorPicker.getColor();
            sendCommand(device, Command.RGB, color.getRed(), color.getGreen(), color.getBlue());
        });

        confirmBrightness.addActionListener(actionEvent -> sendCommand(device, Command.BRIGHTNESS, brightness.getValue()));

        if (temperature != null) {
            confirmTemperature.addActionListener(actionEvent -> sendCommand(device, Command.TEMPERATURE, temperature.getValue()));
            temperature.addMouseMotionListener(new TextSliderAdapter(temperature, temperatureTextField, "K"));
        }

        backButton.addActionListener(actionEvent -> {
            this.close();
            CircleSpin circleSpin = new CircleSpin(props.getProperty(BulberConst.CIRCLE_SPIN_DEVICES_MESSAGE), props.getIntProperty(BulberConst.CIRCLE_SPIN_MAIN_SIZE));

            JFrame frame = (JFrame) SwingUtilities.getRoot((Component) actionEvent.getSource());
            frame.add(circleSpin);
            frame.remove(this);
            frame.pack();
            frame.revalidate();
            frame.repaint();

            Timer timer = new Timer(props.getIntProperty(BulberConst.CIRCLE_SPIN_DELAY), event -> circleSpin.repaint());
            timer.start();

            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {
                    Discover discover;
                    try {
                        discover = new Discover(KasaManager.getDevices());
                    } catch (IOException | InterruptedException e) {
                        JOptionPane.showMessageDialog(frame, props.getProperty(BulberConst.DISCOVER_FIND_ERROR), props.getProperty(BulberConst.DISCOVER_DIALOG_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
                        discover = new Discover(Collections.emptyList());
                    }

                    timer.stop();

                    frame.remove(circleSpin);
                    frame.add(discover);
                    frame.pack();
                    frame.revalidate();
                    frame.repaint();
                    return null;
                }
            }.execute();

        });
    }

    @Override
    public void close() {
        mr.close();
    }

    private void sendCommand(Device device, Command command, Object... params) {
        Map<Command, List<Object>> commands = new LinkedHashMap<>();
        commands.put(command, Arrays.asList(params));
        if (transition.getValue() > 0) {
            commands.put(Command.TRANSITION, Collections.singletonList(seconds.isSelected() ? transition.getValue() * 1000 : transition.getValue()));
        }
        try {
            String cmd = CommandParser.getInstance().parse(device, commands);
            logger.debug(props.getProperty(BulberConst.EXECUTE_KASA_COMMAND), cmd);
            logger.debug(props.getProperty(BulberConst.KASA_COMMAND_RESPONSE), KasaManager.executeCommand(cmd));
        } catch (IOException | InterruptedException e) {
            logger.error(props.getProperty(BulberConst.KASA_COMMAND_ERROR), e);
        }
    }

}
