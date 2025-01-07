@echo off
setlocal enabledelayedexpansion

:: Clean and create bin directory
if exist bin rmdir /s /q bin
mkdir bin

:: Set paths
set PATH_TO_FX=lib
set CLASSPATH="%PATH_TO_FX%/*;src;bin"
set JAVAC_OPTS=--module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.base,javafx.graphics -d bin -cp %CLASSPATH%

:: Echo configuration
echo Building with following settings:
echo JavaFX path: %PATH_TO_FX%
echo Classpath: %CLASSPATH%
echo.

:: Create package directories in bin
mkdir bin\model
mkdir bin\view
mkdir bin\controller

:: Compile files in correct order
echo Compiling Java files...

:: First, compile model classes
javac %JAVAC_OPTS% ^
    src/model/InventoryItem.java ^
    src/model/Database.java ^
    src/controller/InventoryController.java ^
    src/view/InventoryView.java ^
    src/App.java

:: Check for compilation errors
if errorlevel 1 (
    echo.
    echo Compilation failed! Check the errors above.
    exit /b 1
) else (
    echo.
    echo Compilation successful!
    echo.
    dir /s /b bin\*.class
)

pause 