package ru.ag78.api.helpers;

import org.apache.commons.cli.Options;

/**
 * Call-back interface for intialize command line options configuration.
 * @author Alexey Gusev
 *
 */
public interface OptionsInitializer {

    /**
     * Call-back function to initialize command line options configuration.
     * Options -? is alread set by-default.
     * @param opt
     */
    public void initOptions(Options opt);

}
