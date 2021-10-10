package com.demetrio.bulber.view;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.engine.CommandParser;
import com.demetrio.bulber.engine.KasaManager;
import com.demetrio.bulber.engine.VocalNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Discover extends JPanel {

    private static final BulberProperties props = BulberProperties.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(Discover.class);

    private static class DeviceTableModel extends AbstractTableModel {

        public List<Device> getDevices() {
            return devices;
        }

        public void setDevices(List<Device> devices) {
            this.devices = devices;
        }

        private List<Device> devices;

        public DeviceTableModel(List<Device> devices) {
            this.devices = devices;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? props.getProperty(BulberConst.DISCOVER_TABLE_DEVICE) :
                    props.getProperty(BulberConst.DISCOVER_TABLE_ADDRESS);
        }

        @Override
        public int getRowCount() {
            return devices.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Device device = devices.get(rowIndex);
            return columnIndex == 0 ? device.getDeviceName() : device.getAddress();
        }
    }

    public Discover(List<Device> devices) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        DeviceTableModel model = new DeviceTableModel(devices);
        JTable table = new JTable(model);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        JScrollPane scrollPane = new JScrollPane(table);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true),
                props.getProperty(BulberConst.DISCOVER_BORDER_TITLE));
        scrollPane.setBorder(border);

        JPanel buttonPanel = new JPanel();
        JButton confirm = new JButton(props.getProperty(BulberConst.DISCOVER_CONFIRM));
        confirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(confirm);

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel refreshPanel = new JPanel();
        JButton refresh = new JButton(props.getProperty(BulberConst.DISCOVER_REFRESH));
        refresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        int circleSpinSize = props.getIntProperty(BulberConst.CIRCLE_SPIN_DISCOVER_SIZE);
        Component invisible = Box.createRigidArea(new Dimension(circleSpinSize, circleSpinSize));
        refreshPanel.add(refresh);
        refreshPanel.add(invisible);

        JPanel flowLanguagePanel = new JPanel();
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
                            props.changeLanguage(lang);
                            model.fireTableStructureChanged();
                            border.setTitle(props.getProperty(BulberConst.DISCOVER_BORDER_TITLE));
                            scrollPane.revalidate();
                            scrollPane.repaint();
                            confirm.setText(props.getProperty(BulberConst.DISCOVER_CONFIRM));
                            refresh.setText(props.getProperty(BulberConst.DISCOVER_REFRESH));
                            CommandParser.getInstance().refresh();
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
        topPanel.add(flowLanguagePanel, BorderLayout.EAST);
        topPanel.add(refreshPanel, BorderLayout.WEST);

        this.add(topPanel);
        this.add(scrollPane);
        this.add(buttonPanel);

        Consumer<AWTEvent> confirmDevice = event -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                JFrame frame = (JFrame) SwingUtilities.getRoot((Component) event.getSource());
                CircleSpin circleSpin = new CircleSpin(props.getProperty(BulberConst.CIRCLE_SPIN_CONTROL_PANEL_MESSAGE), props.getIntProperty(BulberConst.CIRCLE_SPIN_MAIN_SIZE));

                frame.add(circleSpin);
                frame.remove(this);
                frame.pack();
                frame.revalidate();
                frame.repaint();

                Timer timer = new Timer(props.getIntProperty(BulberConst.CIRCLE_SPIN_DELAY), e -> circleSpin.repaint());
                timer.start();

                new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            ControlPanel controlPanel = new ControlPanel(model.getDevices().get(selected));
                            frame.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    controlPanel.close();
                                }
                            });
                            frame.add(controlPanel);
                            frame.remove(circleSpin);
                            frame.pack();
                            frame.revalidate();
                            frame.repaint();
                        } catch (LineUnavailableException e) {
                            String errorMessage = props.getProperty(BulberConst.MICROPHONE_ERROR);
                            logger.error(errorMessage, e);
                            JOptionPane.showMessageDialog(frame, errorMessage, props.getProperty(BulberConst.DISCOVER_DIALOG_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
                        }
                        return null;
                    }
                }.execute();
            } else {
                JOptionPane.showMessageDialog(this, props.getProperty(BulberConst.DISCOVER_SELECT_ERROR_MESSAGE), props.getProperty(BulberConst.DISCOVER_SELECT_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
            }
        };

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    confirmDevice.accept(e);
                }
            }
        });

        confirm.addActionListener(confirmDevice::accept);

        refresh.addActionListener(actionEvent -> {
            CircleSpin circleSpin = new CircleSpin(circleSpinSize);
            refreshPanel.add(circleSpin);
            refreshPanel.remove(invisible);
            refreshPanel.revalidate();
            refreshPanel.repaint();
            Timer timer = new Timer(props.getIntProperty(BulberConst.CIRCLE_SPIN_DELAY), ae -> circleSpin.repaint());
            timer.start();
            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    model.setDevices(KasaManager.getDevices());
                    model.fireTableDataChanged();
                    return null;
                }

                @Override
                protected void done() {
                    timer.stop();
                    refreshPanel.add(invisible);
                    refreshPanel.remove(circleSpin);
                    refreshPanel.revalidate();
                    refreshPanel.repaint();
                }
            }.execute();
        });
    }
}
