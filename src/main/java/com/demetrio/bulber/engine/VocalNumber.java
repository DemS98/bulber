package com.demetrio.bulber.engine;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VocalNumber {

    private static final BulberProperties props = BulberProperties.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(VocalNumber.class);

    private static final VocalNumber INSTANCE = new VocalNumber();

    private Map<String, Integer> vocalNumberMap;

    private String separator;

    private VocalNumber() {
        initCache();
    }

    public static VocalNumber getInstance() {
        return INSTANCE;
    }

    public void initCache() {
        logger.debug(props.getProperty(BulberConst.VOCAL_START_INIT_CACHE));
        long start = System.currentTimeMillis();

        ValueConverters converter = ValueConverters.valueOf(props.getProperty(BulberConst.VOCAL_CONVERTER));
        vocalNumberMap = new HashMap<>();
        for(int i=0; i <= 15000; i++) {
            String word = converter.asWords(i).replace('-', ' ');
            vocalNumberMap.put(word, i);
        }

        logger.debug(props.getProperty(BulberConst.VOCAL_END_INIT_CACHE), System.currentTimeMillis() - start);
        separator = props.getProperty(BulberConst.COMMAND_VOCAL_RGB_SEPARATOR);
    }

    public String replaceVocalsWithNumbers(String str) {
        if (props.getProperty(BulberConst.LANG).equals("en")) {
            str = str.replaceAll("\\s+and\\s+", " ");
        }
        String[] words = str.split(separator);
        StringBuilder sb = new StringBuilder();

        int commandFirstSpaceIndex = words[0].indexOf(' ');
        if (commandFirstSpaceIndex != -1) {
            sb.append(words[0], 0, commandFirstSpaceIndex);
            sb.append(' ');

            String word = words[0].substring(commandFirstSpaceIndex + 1);
            Integer number = vocalNumberMap.get(word);
            sb.append(number != null ? number : word);
        } else {
            sb.append(words[0]);
        }
        sb.append(' ');

        for(int i=1;i<words.length;i++) {
            Integer number = vocalNumberMap.get(words[i]);
            sb.append(number != null ? number : words[i]);
            sb.append(' ');
        }

        return sb.toString().trim();
    }
}
