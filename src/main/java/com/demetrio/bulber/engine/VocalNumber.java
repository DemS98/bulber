package com.demetrio.bulber.engine;

import com.demetrio.bulber.conf.BulberConst;
import com.demetrio.bulber.conf.BulberProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class VocalNumber {

    private static final BulberProperties props = BulberProperties.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(VocalNumber.class);

    private static final VocalNumber INSTANCE = new VocalNumber();

    private Map<String, Integer> vocalNumberMap;

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
            String word = converter.asWords(i).replace("-", "").replace(" ", "");
            vocalNumberMap.put(word, i);
        }

        logger.debug(props.getProperty(BulberConst.VOCAL_END_INIT_CACHE), System.currentTimeMillis() - start);
    }

    public String replaceVocalsWithNumbers(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

        if (props.getProperty(BulberConst.LANG).equals("en")) {
            str = str.replaceAll("\\s+and\\s+", " ");
        }
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();

        int i=0;
        while (i < words.length) {
            StringBuilder word = new StringBuilder(words[i]);
            Integer number = vocalNumberMap.get(words[i]);
            Integer temp = number;
            while (temp != null && i < words.length - 1) {
                if (words[++i].equals(props.getProperty(BulberConst.COMMAND_VOCAL_NUMBER_SEPARATOR))) {
                    break;
                }
                word.append(words[i]);
                temp = vocalNumberMap.get(word.toString());
                if (temp == null) {
                    i--;
                    break;
                }
                number = temp;
            }

            if (number != null) {
                sb.append(number);
            } else {
                sb.append(word);
            }
            i++;
            sb.append(' ');
        }

        return sb.toString().trim();
    }
}
