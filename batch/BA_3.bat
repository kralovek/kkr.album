@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.group.MainGroup3

%JAVA% -classpath %CLASSPATH% %CLASS%
