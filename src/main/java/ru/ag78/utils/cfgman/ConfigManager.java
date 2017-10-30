package ru.ag78.utils.cfgman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import ru.ag78.api.helpers.OptionsHelper;
import ru.ag78.api.helpers.OptionsInitializer;
import ru.ag78.api.utils.SafeArrays;

/**
 * Start class of ConfigManager
 * @author Алексей
 *
 */
public class ConfigManager implements OptionsInitializer {

    private static Logger log = Logger.getLogger(ConfigManager.class);

    public static class Opts {

        private static final String IGNORE = "i";
        private static final String BATCH = "batch";
        private static final String DAT = "dat";
        private static final String ORIG = "orig";
        private static final String DEST = "dest";
        private static final String SRC = "src";
        private static final String INFO = "info";
        private static final String PROPS = "props";
        private static final String HALT = "halt";
        private static final String CHECK = "chk";
    }

    public static void main(String[] args) {

        try {
            ConfigManager t = new ConfigManager();
            t.start(args);

            System.exit(0);
        } catch (Exception e) {
            log.error(e);
            System.exit(-1);
        }
    }

    @Override
    public void initOptions(Options opts) {

        // -halt
        opts.addOption(Opts.HALT, false, "Halt & cancel work on found unmatched parameters.");

        // -info
        opts.addOption(Opts.INFO, false, "Shows information about dat-file. Option -dat is mandatory.");

        // -props
        opts.addOption(Opts.PROPS, true, "Comma-separated key=value pairs: key1=value1,key2=value2. This values is high priority.");

        // -src, --source <filename>
        opts.addOption(Opts.SRC, "source", true, "Name of source file to be parametrized.");

        // -dest, --destination <filename>
        opts.addOption(Opts.DEST, "destination", true, "Name of destination parametrized file. dest = src if dest is omitted.");

        // -dat <filename>
        opts.addOption(Opts.DAT, true, "Filenames, separated by comma, contained keys and values.");

        // -batch <filename>
        opts.addOption(Opts.BATCH, true, "Name of batch file.");

        // -i, --ignore <param1,param2,...,paramN>
        opts.addOption(Opts.IGNORE, "ignore", true, "List of ignorable parameters, separated by comma.");
        opts.addOption(Opts.ORIG, "original-file", true, "Supply original dat-file to check another. If files are different -1 will be returned.");
        opts.addOption(Opts.CHECK, "check-dat-file", false, "Checks -dat file vs original-file.");
    }

    private void start(String[] args) throws Exception {

        OptionsHelper options = new OptionsHelper(args, this);

        if (options.isHelp()) {
            showHelp(options);
            return;
        }

        if (options.isOption(Opts.INFO)) {
            showInfo(options);
            return;
        }

        if (options.isOption(Opts.BATCH)) {
            String batchFile = options.getOption(Opts.BATCH);
            log.info("Batch mode file=" + batchFile);
            processBatch(batchFile);
            return;
        }

        if (options.isOption(Opts.CHECK)) {
            checkDatFile(options);
            return;
        }

        processSingle(options);
    }

    /**
     * Checks dat file on original
     * @param options
     * @throws Exception
     */
    private void checkDatFile(OptionsHelper options) throws Exception {

        new FileChecker(options.getOption(Opts.DAT), options.getOption(Opts.ORIG)).check();
    }

    /**
     * Outputs help information.
     * @param options
     */
    private void showHelp(OptionsHelper options) {

        Properties props = new Properties();
        try (InputStream is = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF")) {
            props.load(is);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Config Manager command-line tool ").append("v").append(props.getProperty("Version")).append("\r\n");
        sb.append("Use ${cfgman.password[.size][.alphabet]} for automatic generate password. Alphabet is '0aA'.").append("\r\n");
        sb.append("Use ${cfgman.md5} for MD5 generation.");
        options.showHelp("cfgman [<options>]", sb.toString(), "Alexey Gusev 2017");
    }

    /**
     * Shows information for supplied files.
     * @param opts
     * @throws Exception
     */
    private void showInfo(OptionsHelper opts) throws Exception {

        List<String> datFiles = getDatFiles(opts.getOption(Opts.DAT));
        Map<String, String> extProps = getProps(opts.getOption(Opts.PROPS));

        FileProcessor proc = new FileProcessor();
        proc.init(datFiles, opts.isOption(Opts.HALT));
        proc.addExtProps(extProps);
        proc.initIgnoreList(opts.getOption(Opts.IGNORE));

        proc.showDatFileInfo("Final:");

        //        Map<String, String> extProps = getProps(opts.getOption(Opts.PROPS));
        //        Stream<String> s = this.getDatFiles(opts.getOption(Opts.DAT, "")).stream();
        //        s.forEach(x -> {
        //            try {
        //                FileProcessor fp = new FileProcessor();
        //                fp.showDatFileInfo(x);
        //            } catch (Exception e) {
        //                log.error(e);
        //            }
        //        });
    }

    private void processBatch(String batchFile) throws Exception {

        try (FileReader fr = new FileReader(batchFile); BufferedReader rdr = new BufferedReader(fr);) {
            String line = "";
            while ((line = rdr.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                String[] args = line.split("\\s");

                OptionsHelper opts = new OptionsHelper(args, this);
                processSingle(opts);
            }
        }
    }

    /**
     * Process single file.
     * @param opts
     * @throws Exception
     */
    private void processSingle(OptionsHelper opts) throws Exception {

        String src = opts.getOption(Opts.SRC);
        String dest = opts.getOption(Opts.DEST);
        if (dest.isEmpty()) {
            dest = src;
        }

        List<String> datFiles = getDatFiles(opts.getOption(Opts.DAT));
        Map<String, String> extProps = getProps(opts.getOption(Opts.PROPS));

        log.info("src=" + src + " dest=" + dest + " dat=" + datFiles.toString());

        FileProcessor proc = new FileProcessor();
        proc.init(datFiles, opts.isOption(Opts.HALT));
        proc.addExtProps(extProps);
        proc.initIgnoreList(opts.getOption(Opts.IGNORE));
        proc.processFile(src, dest);

        StringBuilder sb = new StringBuilder();
        sb.append("Finished:");
        sb.append(" duration=" + Long.toString(proc.getCntrOk().duration()) + " ms");
        sb.append(", matched=" + Integer.toString(proc.getCntrOk().get()));
        sb.append(", not matched=" + Integer.toString(proc.getCntrFail().get()));
        sb.append(" " + (proc.getCntrFail().get() > 0 ? "XXXXX" : ""));
        log.info(sb.toString());
        log.info("");
    }

    /**
     * Формируем map добавленных свойств на основе параметров -props key1=value1,key2=value2.
     * @param option
     * @return
     */
    private Map<String, String> getProps(String option) {

        Map<String, String> extProps = new HashMap<>();

        String[] pairs = option.split(",");
        if (pairs.length == 0) {
            return extProps;
        }

        for (String pair: pairs) {
            String[] tokens = pair.split("=");
            String key = SafeArrays.getSafeItem(tokens, 0);
            if (key.isEmpty()) {
                continue;
            }

            key = String.format("${%s}", key);
            String value = SafeArrays.getSafeItem(tokens, 1);

            extProps.put(key, value);
        }

        return extProps;
    }

    private List<String> getDatFiles(String option) {

        List<String> list = Arrays.asList(option.split(","));
        return list;
    }
}
