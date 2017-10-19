package ru.ag78.utils.cfgman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import ru.ag78.api.utils.Counter;
import ru.ag78.api.utils.Generator;
import ru.ag78.api.utils.PasswordGenerator;
import ru.ag78.api.utils.SafeArrays;
import ru.ag78.api.utils.SafeTypes;

/**
 * Processor for parametrized files.
 * @author Алексей
 *
 */
public class FileProcessor {

    private static final Logger log = Logger.getLogger(FileProcessor.class);

    private Map<String, String> replacements = new HashMap<>();
    private Set<String> ignoreList = new TreeSet<>();
    private Counter cntrOk = new Counter();
    private Counter cntrFail = new Counter();

    /**
     * При найденных несматченных элементах прекращать работу.
     * По-умолчанию false.
     */
    private boolean haltOnNotMatched = false;

    public void init(String dat, boolean haltOnNotMatched) throws Exception {

        init(Arrays.asList(dat), haltOnNotMatched);
    }

    /**
     * Initializa FileProcessor.
     * @param datFiles
     * @param haltOnNotMatched
     * @throws Exception
     */
    public void init(List<String> datFiles, boolean haltOnNotMatched) throws Exception {

        for (String dat: datFiles) {
            Map<String, String> r = loadDataFile(dat);
            replacements.putAll(r);
        }

        this.haltOnNotMatched = haltOnNotMatched;
    }

    /**
     * Initialize ignore parameters list.
     * @param ignoreStr
     * @throws Exception
     */
    public void initIgnoreList(String ignoreStr) throws Exception {

        String[] tokens = ignoreStr.split(",");
        for (String t: tokens) {
            if (t == null || t.isEmpty()) {
                continue;
            }
            ignoreList.add("${" + t + "}");
        }
    }

    /**
     * Load file with set of properties
     * @param datFile
     * @return
     * @throws Exception
     */
    private Map<String, String> loadDataFile(String datFile) throws Exception {

        Map<String, String> map = new HashMap<>();
        try (FileReader fr = new FileReader(datFile); BufferedReader br = new BufferedReader(fr)) {
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

                // if current parameter parametrized itself, we won't use it further as source.
                if (isParametrized(SafeArrays.getSafeItem(kv, 1).trim())) {
                    continue;
                }

                key = "${" + key + "}";
                String value = SafeArrays.getSafeItem(kv, 1).trim();
                map.put(key, value);
            }
        }

        log.debug("--- data-file ---");
        for (String key: map.keySet()) {
            log.debug("  " + key + "=" + map.get(key));
        }

        return map;
    }

    /**
     * Process config-file
     * @param srcFile
     * @param replacements
     * @throws Exception
     */
    public void processFile(String srcFile, String destFile) throws Exception {

        log.debug("process src=" + srcFile + " dest=" + destFile);

        String file = FileUtils.readFileToString(new File(srcFile));
        final String regex = "\\$\\{[a-zA-Z0-9_\\.]+\\}";

        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(file);
        while (m.find()) {
            String pattern = m.group();
            String repString = replacements.get(pattern);
            if (isPassword(pattern)) {
                int lenght = getPasswordLength(pattern);
                String template = getPasswordTemplate(pattern);
                repString = PasswordGenerator.generate(lenght, template);
            } else if (isMD5(pattern)) {
                String baseString = this.getMD5BaseString(pattern);
                repString = Generator.getNextMD5(baseString);
            }
            log.trace("repString=" + repString);
            try {
                if (repString != null) {
                    repString = Matcher.quoteReplacement(repString);
                    m.appendReplacement(sb, repString);

                    cntrOk.increment();
                    log.trace(pattern + " replaced by {" + repString + "}");
                } else {
                    boolean isIgnoredParam = ignoreList.contains(pattern);
                    if (isIgnoredParam) {
                        // Это игнорируемый параметр - не останавливаться и не выдавать предупреждения
                        continue;
                    }
                    log.warn(pattern + " not matched");
                    cntrFail.increment();
                }
            } catch (Exception e) {
                String out = "Failed to process pattern=" + pattern + " repString=" + repString;
                log.error(out, e);
                throw new Exception(out, e);
            }
        }
        m.appendTail(sb);

        // Есть ли несматченные параметры?
        if (haltOnNotMatched && cntrFail.get() > 0) {
            throw new Exception("Not matched " + Integer.toString(cntrFail.get()) + " params. Halted.");
        }

        if (cntrOk.get() > 0 || srcFile.compareTo(destFile) != 0) {
            writeTo(destFile, sb);
        } else {
            log.info("Write dest skiped.");
        }
    }

    private void writeTo(String destFile, StringBuffer sb) throws Exception {

        if (destFile.equalsIgnoreCase("CON")) {
            System.out.print(sb.toString());
            return;
        }

        try (FileWriter fw = new FileWriter(destFile)) {
            fw.write(sb.toString());
        }
    }

    public void showDatFileInfo(String dat) throws Exception {

        // init(dat, false);

        log.info("--- Info mode: dat=" + dat);
        log.info("options.count=" + replacements.size());

        for (String key: replacements.keySet()) {
            log.info(String.format("  %s=%s", key, replacements.get(key)));
        }
        log.info("");
    }

    /**
     * Returns true if supplied value is parametrized itself.
     * 
     * @param tag
     * @return
     */
    private boolean isParametrized(String tag) {

        return tag.startsWith("${") && tag.endsWith("}");
    }

    private boolean isPassword(String tag) {

        return tag.contains("cfgman.password");
    }

    private boolean isMD5(String tag) {

        return tag.contains("cfgman.md5");
    }

    /**
     * Returns the size of password to be generated.
     * Tag pattern: cfgman.password.<length>.
     * Default value is 10 if lenght has not been set.
     * @param tag
     * @return
     */
    private int getPasswordLength(String tag) {

        tag = tag.replaceAll("[\\$\\{\\}]", "");
        String[] tokens = tag.split("\\.");
        return SafeTypes.parseSafeInt(SafeArrays.getSafeItem(tokens, 2), 10);
    }

    private String getPasswordTemplate(String tag) {

        tag = tag.replaceAll("[\\$\\{\\}]", "");
        String[] tokens = tag.split("\\.");
        return SafeTypes.getSafeString(SafeArrays.getSafeItem(tokens, 3, "aA0"));
    }

    /**
     * Для параметра cfgman.md5.[<base_string>] вытаскивает значение <base_string>.
     * На основе этого параметра будет расчитан md5.
     * По-умолчанию возвращается пустая строка.
     * @param tag
     * @return
     */
    private String getMD5BaseString(String tag) {

        tag = tag.replaceAll("[\\$\\{\\}]", "");
        String[] tokens = tag.split("\\.");
        return SafeTypes.getSafeString(SafeArrays.getSafeItem(tokens, 2, ""));
    }

    public Counter getCntrOk() {

        return cntrOk;
    }

    public Counter getCntrFail() {

        return cntrFail;
    }

    /**
     * Replace extisting properties with supplied new one.
     * @param extProps
     */
    public void addExtProps(Map<String, String> extProps) {

        replacements.putAll(extProps);
    }
}
