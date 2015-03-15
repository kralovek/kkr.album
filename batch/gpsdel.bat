@echo off

IF [%1]==[] (
    echo GPS Disc letter is not specified
    goto :go_end
)

del /Q %1:\Garmin\GPX\Archive\*.gpx
del /Q %1:\Garmin\GPX\*.gpx

:go_end