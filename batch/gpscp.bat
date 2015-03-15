@echo off

IF [%1]==[] (
    echo GPS Disc letter is not specified
    goto :go_end
)

copy %1:\Garmin\GPX\Current\*.gpx .
copy %1:\Garmin\GPX\Archive\*.gpx .
copy %1:\Garmin\GPX\*.gpx .

:go_end