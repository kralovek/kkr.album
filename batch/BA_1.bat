@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.group.MainGroup1

%JAVA% -classpath %CLASSPATH% %CLASS%
