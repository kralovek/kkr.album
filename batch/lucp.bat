@echo on

IF [%1]==[] (
    echo XPERIA Disc letter is not specified
    goto :go_end
)

move %1:\DCIM\110_PANA\*.jpg .
move %1:\DCIM\110_PANA\*.mov .
move %1:\DCIM\110_PANA\*.avi .
move %1:\DCIM\110_PANA\*.mp4 .

:go_end
