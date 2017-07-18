del cfgman.log
del current.dat

rem call cfgman -info -dat stuff\test_env.dat,stuff\test_env.pwd
call cfgman -src stuff\test_env.dat -dest current.dat -dat stuff\test_env.dat,stuff\test_env.pwd -props transporter.host=cedr34,transporter.port=5001,transporter.password=fuck
call cfgman -info -src stuff\myconf.xml.template -dest stuff\myconf.xml -dat current.dat
rem call cfgman -src stuff\current.dat -dest stuff\current.dat -dat stuff\current.dat
rem call cfgman -src stuff\current.dat -dest stuff\current.dat -dat stuff\test_env.dat
