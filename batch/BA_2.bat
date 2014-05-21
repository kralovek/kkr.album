@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.group.MainGroup2

%JAVA% -classpath %CLASSPATH% %CLASS%
