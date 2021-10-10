package com.demetrio.bulber.view;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class LCDisplay extends JTextArea {

    public static final BulberProperties props = BulberProperties.getInstance();
    public static final Logger logger = LoggerFactory.getLogger(LCDisplay.class);

    public LCDisplay() {
        super(1, props.getIntProperty(BulberConst.LCDISPLAY_COLUMNS));
        setEditable(false);
        String lcdFontPath = props.getProperty(BulberConst.LCD_FONT_PATH);
        try(InputStream inputStream = new BufferedInputStream(Objects.requireNonNull(LCDisplay.class.getClassLoader()
                .getResourceAsStream(lcdFontPath)))) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            setFont(font.deriveFont(Font.PLAIN, 55));
            setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            setForeground(Color.GREEN);
            setBackground(Color.BLUE);
        } catch (IOException | FontFormatException e) {
            logger.error(props.getProperty(BulberConst.FONT_ERROR), lcdFontPath, e);
        }
    }

}
