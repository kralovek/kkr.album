@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.MainRenameON

%JAVA% -classpath %CLASSPATH% %CLASS% %1 %2 %3 %4
