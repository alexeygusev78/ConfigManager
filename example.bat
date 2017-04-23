@echo off
rem example1
rem  stuff\myconf.xml - parametrized config-file (source file)
rem  stuff\myconf_out.xml - output file (destination file)
rem  stuff\test_env.dat - data-file with paremeters
rem  stuff\test_env.pwd - second data-file with paremeters

del stuff\myconf_out.xml
call cfgman -src stuff\myconf.xml -dest stuff\myconf_out.xml -dat stuff\test_env.dat,stuff\test_env.pwd
