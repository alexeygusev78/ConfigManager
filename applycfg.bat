del cfgman.log
del current.dat

call cfgman -info -dat staff\test_env.dat,staff\test_env.pwd
call cfgman -src staff\test_env.dat -dest staff\current.dat -dat staff\test_env.dat,staff\test_env.pwd
call cfgman -src staff\current.dat -dest staff\current.dat -dat staff\current.dat
rem call cfgman -src staff\current.dat -dest staff\current.dat -dat staff\test_env.dat
