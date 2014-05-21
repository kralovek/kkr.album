@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.group.MainGroup23

%JAVA% -classpath %CLASSPATH% %CLASS%
