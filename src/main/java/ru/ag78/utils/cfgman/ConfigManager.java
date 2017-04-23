package ru.ag78.utils.cfgman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import ru.ag78.api.helpers.OptionsHelper;
import ru.ag78.api.helpers.OptionsInitializer;

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
        private static final String DEST = "dest";
        private static final String SRC = "src";
        private static final String INFO = "info";
        private static final String HALT = "halt";
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
    }

    private void start(String[] args) throws Exception {

        OptionsHelper options = new OptionsHelper(args, this);

        if (options.isHelp()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Config Manager command-line tool.").append("\r\n");
            sb.append("Use ${cfgman.password[.size][.alphabet]} for automatic generate password. Alphabet is '0aA'.").append("\r\n");
            sb.append("Use ${cfgman.md5} for MD5 generation.");
            options.showHelp("cfgman [<options>]", sb.toString(), "Alexey Gusev 2017");
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

        processSingle(options);
    }

    private void showInfo(OptionsHelper opts) throws Exception {

        Stream<String> s = this.getDatFiles(opts.getOption(Opts.DAT, "")).stream();
        s.forEach(x -> {
            try {
                FileProcessor fp = new FileProcessor();
                fp.showDatFileInfo(x);
            } catch (Exception e) {
            }
        });
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

    private void processSingle(OptionsHelper opts) throws Exception {

        String src = opts.getOption(Opts.SRC);
        String dest = opts.getOption(Opts.DEST);
        // String dat = opts.getOption(Opts.DAT);
        List<String> datFiles = getDatFiles(opts.getOption(Opts.DAT));

        log.info("src=" + src + " dest=" + dest + " dat=" + datFiles.toString());

        FileProcessor proc = new FileProcessor();
        proc.init(datFiles, opts.isOption(Opts.HALT));
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

    private List<String> getDatFiles(String option) {

        List<String> list = Arrays.asList(option.split(","));
        return list;
    }
}
