@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.MainModifyGpx

%JAVA% -classpath %CLASSPATH% %CLASS% %1 %2 %3 %4