@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.MainCreateDate

%JAVA% -classpath %CLASSPATH% %CLASS% %1 %2 %3 %4
