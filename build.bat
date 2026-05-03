@echo off
setlocal

:: Ticket to Ride London - Windows Build Script

set JAR_NAME=TicketToRideLondon.jar
set MAIN_CLASS=Main
set SRC_DIR=src
set RES_DIR=resources
set OUT_DIR=target\classes

echo Cleaning old build artifacts...
if exist %OUT_DIR% rd /s /q %OUT_DIR%
if exist %JAR_NAME% del %JAR_NAME%

echo Creating build directory...
mkdir %OUT_DIR%

echo Compiling source files...
:: Find all Java files recursively and write to sources.txt
dir /s /b "%SRC_DIR%\*.java" > sources.txt
javac --release 17 -d %OUT_DIR% @sources.txt
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    del sources.txt
    pause
    exit /b %ERRORLEVEL%
)
del sources.txt

echo Copying resources...
xcopy /e /y "%RES_DIR%\*" "%OUT_DIR%\" > nul

echo Creating runnable JAR...
jar cfe %JAR_NAME% %MAIN_CLASS% -C %OUT_DIR% .

if %ERRORLEVEL% equ 0 (
    echo Build successful: %JAR_NAME%
    echo You can run it with: java -jar %JAR_NAME%
) else (
    echo Failed to create JAR!
)

pause
