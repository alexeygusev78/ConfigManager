# ConfigManager

A Command-line tool for automatically parametrize your config-files.

## Usage:
`cfgman.bat [options] // for Windows`
or 
`cfgman.sh [options] // for Linux`

        -?                         Show help information
        -batch <arg>               Name of batch file.
        -dat <arg>                 Filenames, separated by comma, contained keys and values.
        -dest,--destination <arg>  Name of destination parametrized file. dest = src if dest is
                                   omitted.
        -halt                      Halt & cancel work on found unmatched parameters.
        -i,--ignore <arg>          List of ignorable parameters, separated by comma.
        -info                      Shows information about dat-file. Option -dat is mandatory.
        -src,--source <arg>        Name of source file to be parametrized.

## How to build:
`gradle clean fatJar`

## Examples1: Set parameters values for file 