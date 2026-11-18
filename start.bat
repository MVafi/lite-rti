@echo off
setlocal

cd /d "%~dp0"

where mvn >nul 2>nul
if errorlevel 1 (
    echo Maven is required but was not found on PATH.
    exit /b 1
)

call mvn -q -DskipTests package
if errorlevel 1 (
    echo Maven build failed.
    exit /b 1
)

java -cp "target\classes;target\dependency\*" nl.literti.LiteRtiApplication %*
exit /b %ERRORLEVEL%