package ru.ag78.api.helpers;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import ru.ag78.api.utils.SafeTypes;

/**
 * Class for parsing & handle program options based on Apache Commons CLI.
 * @author Alexey Gusev
 *
 */
public class OptionsHelper {

    private CommandLine cmd;
    private Options options;

    public final static String HELP_OPTION = "h";

    /**
     * Ctor with command-line arguments and OptionsInitializer ref for callback.
     * @param args
     */
    public OptionsHelper(String[] args, OptionsInitializer oi) throws Exception {

        if (oi == null) {
            throw new IllegalArgumentException("OptionsInitializer cannot be null");
        }

        options = new Options();
        options.addOption(HELP_OPTION, "help", false, "Show help information");
        oi.initOptions(options);

        cmd = parseCommandLine(options, args);
    }

    private CommandLine parseCommandLine(Options options, String[] args) throws Exception {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        return cmd;
    }

    /**
     * Safely returns value of supplied option.
     * If error returns empty string.
     * @param name
     * @return
     */
    public String getOption(String name) {

        return getOption(name, "");
    }

    /**
     * Safely returns value of supplied option.
     * If error returns supplied defaultValue.
     * @param name
     * @param defaultValue
     * @return
     */
    public String getOption(String name, String defaultValue) {

        if (isOption(name)) {

            return SafeTypes.getSafeString(cmd.getOptionValue(name), defaultValue);
        }

        return defaultValue;
    }

    /**
     * Safely checks if supplied option is really exists in command line.
     * @param name
     * @return
     */
    public boolean isOption(String name) {

        return cmd.hasOption(name);
    }

    /**
     * Show help-information.
     * @param cmdLineSyntax - something like 'java -jar LoadTool.jar [<options>]...'
     * @param header
     * @param footer
     */
    public void showHelp(String cmdLineSyntax, String header, String footer) {

        try (PrintWriter pw = new PrintWriter(System.out, true);) {
            showHelp(cmdLineSyntax, header, footer, pw);
        } catch (Exception e) {
            System.err.print(e);
        }
    }

    /**
     * Shows help-information into supplied PrintWriter.
     * @param cmdLineSyntax
     * @param header
     * @param footer
     * @param pw
     */
    public void showHelp(String cmdLineSyntax, String header, String footer, PrintWriter pw) {

        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(pw, 100, cmdLineSyntax, header, options, 8, 2, footer);
    }

    /**
     * Checks if option -? is exists in the command line.
     * @return
     */
    public boolean isHelp() {

        return cmd.hasOption(HELP_OPTION);
    }
}
