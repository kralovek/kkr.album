@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.group.MainGroupA

%JAVA% -classpath %CLASSPATH% %CLASS%
