package com.demetrio.bulber;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.engine.KasaManager;
import com.demetrio.bulber.engine.VocalNumber;
import com.demetrio.bulber.view.CircleSpin;
import com.demetrio.bulber.view.Discover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.LibVosk;
import org.vosk.LogLevel;

import javax.swing.*;
import java.io.IOException;
import java.util.Collections;

public class BulberApplication {

    private static final Logger logger = LoggerFactory.getLogger(BulberApplication.class);

    private static final BulberProperties props = BulberProperties.getInstance();

    static {
        System.setProperty("sun.java2d.opengl", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            logger.error(props.getProperty(BulberConst.LOOK_AND_FEEL), e);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        LibVosk.setLogLevel(LogLevel.WARNINGS);

        CircleSpin circleSpin = new CircleSpin(props.getProperty(BulberConst.CIRCLE_SPIN_DEVICES_MESSAGE), props.getIntProperty(BulberConst.CIRCLE_SPIN_MAIN_SIZE));

        JFrame frame = new JFrame(props.getProperty(BulberConst.APPLICATION_NAME));
        frame.add(circleSpin);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(props.getIntProperty(BulberConst.CIRCLE_SPIN_DELAY), actionEvent -> circleSpin.repaint());
        timer.start();

        String classToInit = VocalNumber.class.getName();
        logger.info(props.getProperty(BulberConst.INIT_CLASS), classToInit);
        Class.forName(classToInit);

        Discover discover;
        try {
            discover = new Discover(KasaManager.getDevices());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, props.getProperty(BulberConst.DISCOVER_FIND_ERROR), props.getProperty(BulberConst.DISCOVER_DIALOG_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
            discover = new Discover(Collections.emptyList());
        }
        timer.stop();

        frame.remove(circleSpin);
        frame.add(discover);
        frame.pack();
        frame.revalidate();
        frame.repaint();
    }

}
