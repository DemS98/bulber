package com.demetrio.bulber.engine;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import com.demetrio.bulber.engine.bulb.LightState;
import com.demetrio.bulber.engine.bulb.Request;
import com.demetrio.bulber.view.Device;
import com.demetrio.bulber.view.UIUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MicrophoneRecognition implements Runnable, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(MicrophoneRecognition.class);
    private static final BulberProperties props = BulberProperties.getInstance();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final VocalNumber vocalNumber = VocalNumber.getInstance();
    private static final CommandParser commandParser = CommandParser.getInstance();

    private final Consumer<String> setLCDisplayText;
    private final Device device;

    private Model model;
    private Recognizer recognizer;
    private final TargetDataLine microphone;
    private ScheduledExecutorService easterEgg1Timer;

    private final int chunkSize;
    private final String resultProp;
    private final UIUpdater uiUpdater;

    public MicrophoneRecognition(Device device, UIUpdater updater, Consumer<String> setLCDisplayText) throws LineUnavailableException, IOException {
        this.setLCDisplayText = setLCDisplayText;
        this.device = device;
        this.uiUpdater = updater;

        model = new Model(props.getProperty(BulberConst.MODELS_PATH));
        recognizer = new Recognizer(model, props.getIntProperty(BulberConst.RECOGNIZER_AUDIO_SAMPLE_RATE));

        chunkSize = props.getIntProperty(BulberConst.RECOGNIZER_MICROPHONE_READ_BUFFER_SIZE);
        resultProp = props.getProperty(BulberConst.RECOGNIZER_RESULT_PROPERTY);

        AudioFormat format = new AudioFormat(props.getFloatProperty(BulberConst.RECOGNIZER_AUDIO_SAMPLE_RATE), props.getIntProperty(BulberConst.RECOGNIZER_AUDIO_SAMPLE_RATE_SIZE_BITS),
                props.getIntProperty(BulberConst.RECOGNIZER_AUDIO_CHANNELS), props.getBooleanProperty(BulberConst.RECOGNIZER_AUDIO_SIGNED),
                props.getBooleanProperty(BulberConst.RECOGNIZER_AUDIO_BIGENDIAN));

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
    }

    @Override
    public void run() {
        try {
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();
            microphone.flush();
            microphone.drain();

            logger.debug(props.getProperty(BulberConst.START_RECOGNIZING));
            int bytesRead;
            while ((bytesRead = microphone.read(data, 0, chunkSize)) > 0) {
                if (recognizer.acceptWaveForm(data, bytesRead)) {
                    String received = MAPPER.readTree(recognizer.getResult()).get(resultProp).asText();
                    if (!received.isEmpty() && !easterEgg1(received, uiUpdater.isDeviceOn())) {
                        logger.debug(props.getProperty(BulberConst.PROCESSING_STRING), received);
                        String numberSubstituted = vocalNumber.replaceVocalsWithNumbers(received);
                        ResultAction result = commandParser.parse(device, numberSubstituted, uiUpdater);
                        if (result != null) {
                            setLCDisplayText.accept(numberSubstituted);
                            logger.debug(props.getProperty(BulberConst.EXECUTE_KASA_COMMAND), result.getRequest());
                            logger.debug(props.getProperty(BulberConst.KASA_COMMAND_RESPONSE), KasaManager.execute(device, result.getRequest()));
                            result.getUiTasks().forEach(Runnable::run);
                        } else {
                            String response = props.getProperty(BulberConst.INVALID_COMMAND);
                            logger.trace(response);
                        }
                    }
                }
            }
            logger.debug(props.getProperty(BulberConst.STOP_RECOGNIZING));
        } catch (JsonProcessingException e) {
            logger.error(props.getProperty(BulberConst.JSON_READ_ERROR), e);
        } catch (IOException e) {
            logger.error(props.getProperty(BulberConst.KASA_COMMAND_ERROR), e);
        }
    }

    public void stop() {
        microphone.stop();
        microphone.flush();
        microphone.drain();
    }

    @Override
    public void close() {
        if (microphone.isOpen()) {
            microphone.close();
            recognizer.close();
            model.close();
        }
    }

    public void refresh() throws IOException {
        recognizer.close();
        model.close();
        model = new Model(props.getProperty(BulberConst.MODELS_PATH));
        recognizer = new Recognizer(model, props.getIntProperty(BulberConst.RECOGNIZER_AUDIO_SAMPLE_RATE));
    }

    private boolean easterEgg1(String received, boolean on) {
        if (on) {
            if (received.equals("disco")) {
                if (easterEgg1Timer == null) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    easterEgg1Timer = Executors.newSingleThreadScheduledExecutor();
                    easterEgg1Timer.scheduleAtFixedRate(() -> {
                        LightState changeState = new LightState();
                        int[] hsv = commandParser.fromRGBToHSV(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                        changeState.setHue(hsv[0]);
                        changeState.setSaturation(hsv[1]);
                        changeState.setBrightness(hsv[2]);
                        changeState.setTransitionPeriod(0);
                        changeState.setColorTemp(0);
                        try {
                            KasaManager.execute(device, new Request(new Request.Service(changeState)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, 0, 500, TimeUnit.MILLISECONDS);
                    setLCDisplayText.accept("Disco");
                    return true;
                }
            } else if (received.equals("ferma disco")) {
                if (easterEgg1Timer != null) {
                    easterEgg1Timer.shutdownNow();
                    easterEgg1Timer = null;
                    setLCDisplayText.accept("Ferma disco");
                    return true;
                }
            }
        }
        return false;
    }

}
