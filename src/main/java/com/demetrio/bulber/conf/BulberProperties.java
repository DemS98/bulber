package com.demetrio.bulber.conf;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

public class BulberProperties {

    private static final Logger logger = LoggerFactory.getLogger(BulberProperties.class);

    private static final BulberProperties INSTANCE = new BulberProperties();

    private final CompositeConfiguration configuration;
    private Configuration i18nConfiguration;

    private BulberProperties() {
        configuration = new CompositeConfiguration();
        try {
            configuration.addConfiguration(new Configurations().propertiesBuilder()
                    .configure(new Parameters().properties()
                            .setFileName(BulberConst.APPLICATION_PROPERTIES)
                            .setListDelimiterHandler(new DefaultListDelimiterHandler(BulberConst.ARRAY_DELIMITER)))
                    .getConfiguration());
            String lang = getSupportedLanguages().stream()
                            .filter(Locale.getDefault().getLanguage()::equals).findFirst().orElse(Locale.ENGLISH.getLanguage());
            logger.debug("Lang = {}", lang);
            configuration.addConfiguration((i18nConfiguration = new Configurations().properties(BulberConst.I18N_PROPERTIES.replace("{}",lang))));
        } catch (ConfigurationException e) {
            logger.error("Error reading bulber configuration file", e);
            System.exit(1);
        }
    }

    public static BulberProperties getInstance() {
        return INSTANCE;
    }

    public String getProperty(String key) {
        return configuration.getString(key, "unknown");
    }

    public Integer getIntProperty(String key) {
        return configuration.getInt(key, 0);
    }

    public Float getFloatProperty(String key) {
        return configuration.getFloat(key, 0f);
    }

    public Boolean getBooleanProperty(String key) {
        return configuration.getBoolean(key, false);
    }

    public List<String> getSupportedLanguages() { return configuration.getList(String.class, BulberConst.SUPPORTED_LANGS); }

    public void changeLanguage(String lang) {
        configuration.removeConfiguration(i18nConfiguration);
        try {
            configuration.addConfiguration((i18nConfiguration = new Configurations().properties(BulberConst.I18N_PROPERTIES.replace("{}",
                    lang))));
        } catch (ConfigurationException e) {
            logger.error("Error reading bulber configuration file", e);
            System.exit(1);
        }
    }
}
