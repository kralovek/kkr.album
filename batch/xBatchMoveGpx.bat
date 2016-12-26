@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.MainMoveGpx

%JAVA% -classpath %CLASSPATH% %CLASS% %1 %2 %3 %4 %5 %6 %7 %8 %9
