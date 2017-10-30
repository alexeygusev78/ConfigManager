package ru.ag78.utils.cfgman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ru.ag78.api.utils.SafeArrays;
import ru.ag78.api.utils.SafeTypes;

public class FileChecker {

    private static final Logger log = Logger.getLogger(FileChecker.class);

    private String datFile;
    private String origFile;

    /**
     * Ctor with parameters
     * @param datFile
     * @param origFile
     */
    public FileChecker(String datFile, String origFile) {

        super();
        this.datFile = datFile;
        this.origFile = origFile;
    }

    public void check() throws Exception {

        log.info("Checking your dat-files...");

        Map<String, String> orig = loadDatFile(origFile);
        Map<String, String> dat = loadDatFile(datFile);

        log.info(String.format("There are %d parameters in original-file (%s)", orig.keySet().size(), origFile));
        log.info(String.format("There are %d parameters in your dat-file (%s)", dat.keySet().size(), datFile));
        log.info("");

        // найти неизвестные параметры в dat-файле
        List<String> unknownKeys = findUnknownKeys(orig.keySet(), dat.keySet());
        int unknownKeysCount = unknownKeys.size();
        if (unknownKeysCount > 0) {
            log.warn(String.format("Found %d unknown keys in your dat file:", unknownKeysCount));
            unknownKeys.stream().forEach(k -> log.info("  " + k));
        }
        log.info("");

        // найти параметры, которые отсутствуют в dat-файле
        List<String> missingKeys = findUnknownKeys(dat.keySet(), orig.keySet());
        int missingKeysCount = missingKeys.size();
        if (missingKeysCount > 0) {
            String is = (missingKeysCount > 1) ? "are" : "is";
            String param = (missingKeysCount > 1) ? "parameters" : "parameter";
            log.warn(String.format("There %s %d %s missing in your dat-file!!!", is, missingKeysCount, param));
            missingKeys.stream().forEach(k -> log.info("  " + k));
        }

        if (unknownKeysCount + missingKeysCount > 0) {
            throw new Exception("Check dat-files FAILED! unknownKeysCount=" + Integer.toString(unknownKeysCount) + " missingKeysCount=" + Integer.toString(missingKeysCount));
        }

        log.info("Check dat-files SUCCEEDED! Your dat-file is correct!");
    }

    /**
     * Найти ключи в наборе dat, которые отсутствуют в наборе orig.
     * @param orig
     * @param dat
     * @return
     */
    private List<String> findUnknownKeys(Set<String> orig, Set<String> dat) {

        return dat.stream().filter(s -> !orig.contains(s)).collect(Collectors.toList());
    }

    private Map<String, String> loadDatFile(String filename) throws Exception {

        Map<String, String> map = new HashMap<>();
        try (FileReader fr = new FileReader(filename); BufferedReader br = new BufferedReader(fr)) {
            String line = "";
            while ((line = br.readLine()) != null) {
                line = SafeTypes.getDBString(line);
                // commented lines starts with # character
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                String[] kv = line.split("=");

                String key = SafeArrays.getSafeItem(kv, 0).trim();
                if (key.isEmpty()) {
                    continue;
                }

                String value = SafeArrays.getSafeItem(kv, 1).trim();
                map.put(key, value);
            }
        }

        return map;
    }
}
