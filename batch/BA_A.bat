@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.group.MainGroupA

echo CLASSPATH: %CLASSPATH%

%JAVA% -classpath %CLASSPATH% %CLASS%
