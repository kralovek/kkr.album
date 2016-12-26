@echo off

IF [%1]==[] (
    echo XPERIA Disc letter is not specified
    goto :go_end
)

move %1:\DCIM\100ANDRO\*.jpg .
move %1:\DCIM\100ANDRO\*.mov .
move %1:\DCIM\100ANDRO\*.avi .
move %1:\DCIM\100ANDRO\*.mp4 .

:go_end
