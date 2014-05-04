@echo on

call BatchENV.cmd

set CLASS=kkr.album.main.MainModifyGpx

echo %JAVA% -classpath %CLASSPATH% %CLASS% %1 %2 %3 %4 %5 %6 %7 %8 %9
%JAVA% -classpath %CLASSPATH% %CLASS% %1 %2 %3 %4 %5 %6 %7 %8 %9
