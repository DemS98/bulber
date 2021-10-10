package com.demetrio.bulber.conf;

public interface BulberConst {

    // properties file consts
    String APPLICATION_PROPERTIES = "application.properties";
    String I18N_PROPERTIES = "i18n/{}.properties";
    char ARRAY_DELIMITER = ',';

    // bulber properties consts

    // application name const
    String APPLICATION_NAME = "application.name";

    // supported locales
    String SUPPORTED_LANGS = "supported.langs";

    // main properties consts
    String CIRCLE_SPIN_DELAY = "circle.spin.delay";
    String CIRCLE_SPIN_MAIN_SIZE = "circle.spin.main.size";

    // discover properties consts
    String CIRCLE_SPIN_DISCOVER_SIZE = "circle.spin.discover.size";

    // python-kasa command consts
    String COMMAND_COLOR = "command.color";
    String COMMAND_BRIGHTNESS = "command.brightness";
    String COMMAND_TRANSITION = "command.transition";
    String COMMAND_TEMPERATURE = "command.temperature";
    String COMMAND_LIGHT_ON = "command.light.on";
    String COMMAND_LIGHT_OFF = "command.light.off";
    String COMMAND_KASA = "command.kasa";
    String COMMAND_DISCOVER = "command.discover";
    String COMMAND_TEMPERATURE_RANGE = "command.temperature.range";

    // kasa manager properties consts
    String DEVICE_NAME_PROPERTY = "device.name.property";
    String DEVICE_NAME_REGEX = "device.name.regex";
    String DEVICE_ADDRESS_PROPERTY = "device.address.property";
    String DEVICE_ADDRESS_REGEX = "device.address.regex";
    String DEVICE_STATE_PROPERTY = "device.state.property";
    String DEVICE_STATE_REGEX = "device.state.regex";
    String DEVICE_TEMPERATURE_PROPERTY = "device.temperature.property";
    String DEVICE_TEMPERATURE_REGEX = "device.temperature.regex";
    String DEVICE_MIN_TEMPERATURE_PROPERTY = "device.min.temperature.property";
    String DEVICE_MAX_TEMPERATURE_PROPERTY = "device.max.temperature.property";
    String DEVICE_TEMPERATURE_RANGE_REGEX = "device.temperature.range.regex";

    // control panel properties consts
    String BRIGHTNESS_MIN_VALUE = "brightness.min.value";
    String BRIGHTNESS_DEFAULT_VALUE = "brightness.default.value";
    String BRIGHTNESS_MAX_VALUE = "brightness.max.value";
    String TRANSITION_MIN_VALUE = "transition.min.value";
    String TRANSITION_MAX_VALUE = "transition.max.value";

    // recognizer properties consts
    String RECOGNIZER_AUDIO_SAMPLE_RATE = "recognizer.audio.sample.rate";
    String RECOGNIZER_AUDIO_SAMPLE_RATE_SIZE_BITS = "recognizer.audio.sample.size.bits";
    String RECOGNIZER_AUDIO_CHANNELS = "recognizer.audio.channels";
    String RECOGNIZER_AUDIO_SIGNED = "recognizer.audio.signed";
    String RECOGNIZER_AUDIO_BIGENDIAN = "recognizer.audio.bigendian";
    String RECOGNIZER_MICROPHONE_READ_BUFFER_SIZE = "recognizer.microphone.read.buffer.size";
    String RECOGNIZER_RESULT_PROPERTY = "recognizer.result.property";
    String RECOGNIZER_COMMAND_REGEX = "recognizer.command.regex";

    // lcdisplay properties
    String LCDISPLAY_COLUMNS = "lcdisplay.columns";
    String LCDISPLAY_MAX_VELOCITY = "lcdisplay.max.velocity";
    String LCDISPLAY_DEFAULT_VELOCITY = "lcdisplay.default.velocity";
    String LCDISPLAY_MIN_VELOCITY = "lcdisplay.min.velocity";

    // icon/font properties consts
    String MIC_ICON_PATH = "mic.icon.path";
    String MIC_ON_ICON_PATH = "mic.on.icon.path";
    String MIC_OFF_ICON_PATH = "mic.off.icon.path";
    String MIC_WIDTH = "mic.width";
    String MIC_HEIGHT = "mic.height";
    String LCD_FONT_PATH = "lcd.font.path";
    String BULB_ON = "bulb.on";
    String BULB_OFF = "bulb.off";
    String BULB_WIDTH = "bulb.width";
    String BULB_HEIGHT = "bulb.height";
    String BACK_PATH = "back.path";
    String BACK_SIZE = "back.size";
    String LANG_FLAG_PATH = "lang.flag.path";
    String LANG_FLAG_WIDTH = "lang.flag.width";
    String LANG_FLAG_HEIGHT = "lang.flag.height";

    // i18n properties consts

    String LANG = "lang";

    // log properties

    // main
    String INIT_CLASS = "init.class";
    String LOOK_AND_FEEL = "look.and.feel";

    // discover
    String MICROPHONE_ERROR = "microphone.error";

    // recognizer
    String START_RECOGNIZING = "start.recognizing";
    String PROCESSING_STRING = "processing.string";
    String EXECUTE_KASA_COMMAND = "execute.kasa.command";
    String KASA_COMMAND_RESPONSE = "kasa.command.response";
    String INVALID_COMMAND = "invalid.command";
    String STOP_RECOGNIZING = "stop.recognizing";
    String JSON_READ_ERROR = "json.read.error";
    String KASA_COMMAND_ERROR = "kasa.command.error";

    // vocal number
    String VOCAL_START_INIT_CACHE = "vocal.start.init.cache";
    String VOCAL_END_INIT_CACHE = "vocal.end.init.cache";

    // lcdisplay
    String FONT_ERROR = "font.error";
    String UNEXPECTED_ERROR = "unexpected.error";

    // GUI properties

    // discover
    String CIRCLE_SPIN_DEVICES_MESSAGE = "circle.spin.devices.message";
    String CIRCLE_SPIN_CONTROL_PANEL_MESSAGE = "circle.spin.control.panel.message";
    String DISCOVER_BORDER_TITLE = "discover.border.title";
    String DISCOVER_CONFIRM = "discover.confirm";
    String DISCOVER_REFRESH = "discover.refresh";
    String DISCOVER_TABLE_DEVICE = "discover.table.device";
    String DISCOVER_TABLE_ADDRESS = "discover.table.address";
    String DISCOVER_DIALOG_ERROR_TITLE = "discover.dialog.error.title";
    String DISCOVER_SELECT_ERROR_TITLE = "discover.select.error.title";
    String DISCOVER_SELECT_ERROR_MESSAGE = "discover.select.error.message";
    String DISCOVER_FIND_ERROR = "discover.find.error";

    // control panel
    String MICROPHONE_BORDER_TITLE = "microphone.border.title";
    String MICROPHONE_ON_ALWAYS = "microphone.on.always";
    String LCDISPLAY_MAX_VELOCITY_LABEL = "lcdisplay.max.velocity.label";
    String LCDISPLAY_MIN_VELOCITY_LABEL = "lcdisplay.min.velocity.label";
    String TRANSITION_SECONDS = "transition.seconds";
    String TRANSITION_MILLISECONDS = "transition.milliseconds";
    String BUTTON_CONFIRM = "button.confirm";
    String COMMAND_PANEL_BORDER_TITLE = "command.panel.border.title";
    String COLOR_PICKER_BORDER_TITLE = "color.picker.border.title";
    String BRIGHTNESS_BORDER_TITLE = "brightness.border.title";
    String TRANSITION_BORDER_TITLE = "transition.border.title";
    String TEMPERATURE_BORDER_TITLE = "temperature.border.title";

    // recognizer properties

    // vocal number
    String VOCAL_CONVERTER = "vocal.converter";

    // command parser
    String COMMAND_VOCAL_COLOR = "command.vocal.color";
    String COMMAND_VOCAL_BRIGHTNESS = "command.vocal.brightness";
    String COMMAND_VOCAL_TRANSITION = "command.vocal.transition";
    String COMMAND_VOCAL_TRANSITION_SECONDS = "command.vocal.transition.seconds";
    String COMMAND_VOCAL_TEMPERATURE = "command.vocal.temperature";
    String COMMAND_VOCAL_RGB = "command.vocal.rgb";
    String COMMAND_VOCAL_RGB_SEPARATOR = "command.vocal.rgb.separator";
    String COMMAND_VOCAL_LIGHT_ON = "command.vocal.light.on";
    String COMMAND_VOCAL_LIGHT_OFF = "command.vocal.light.off";

    // standard colors
    String COLOR_BLACK = "color.black";
    String COLOR_BLUE = "color.blue";
    String COLOR_MAGENTA = "color.magenta";
    String COLOR_CYAN = "color.cyan";
    String COLOR_GRAY = "color.gray";
    String COLOR_GREEN = "color.green";
    String COLOR_ORANGE = "color.orange";
    String COLOR_PINK = "color.pink";
    String COLOR_RED = "color.red";
    String COLOR_WHITE = "color.white";
    String COLOR_YELLOW = "color.yellow";

    // recognizer
    String MODELS_PATH = "models.path";
}
