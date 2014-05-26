@echo off

call BatchENV.cmd

set CLASS=kkr.album.main.MainResizePhotos

%JAVA% -classpath %CLASSPATH% %CLASS% %1 %2 %3 %4
