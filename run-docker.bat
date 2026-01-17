@echo off
REM Script to run Durak game with Docker on Windows

REM Build only if -b flag is provided or image doesn't exist
if "%1"=="-b" (
    echo Building Docker image...
    docker build -t durak:v1 .
) else (
    docker image inspect durak:v1 >nul 2>&1
    if errorlevel 1 (
        echo Building Docker image...
        docker build -t durak:v1 .
    )
)

echo.
echo Choose mode: [1] GUI  [2] TUI
set /p choice="Choice: "

if "%choice%"=="1" (
    echo Starting in GUI mode...
    echo Make sure X Server is running (VcXsrv, Xming, X410)
    docker run -it --rm -e DISPLAY=host.docker.internal:0 durak:v1
) else (
    echo Starting in TUI mode...
    docker run -it --rm durak:v1
)
