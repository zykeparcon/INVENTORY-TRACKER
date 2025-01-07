@echo off
setlocal enabledelayedexpansion

:: Clean previous builds
rmdir /s /q out 2>nul
mkdir out
mkdir out\classes

:: Set paths (update these according to your system)
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH_TO_FX=lib

:: Create directories
mkdir out\app
mkdir out\app\lib
mkdir out\app\lib\bin

:: Compile
echo Compiling Java files...
"%JAVA_HOME%\bin\javac" --release 17 --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.base,javafx.graphics -d out\classes src/model/*.java src/view/*.java src/controller/*.java src/App.java

:: Copy dependencies
echo Copying dependencies...
xcopy /y /s lib\* out\app\lib\
mkdir out\app\lib\bin
xcopy /y lib\bin\* out\app\lib\bin\

:: Create manifest
echo Creating manifest...
(
echo Manifest-Version: 1.0
echo Main-Class: App
echo Class-Path: lib/javafx.base.jar lib/javafx.controls.jar lib/javafx.graphics.jar lib/sqlite-jdbc-3.42.0.0.jar
)> out\classes\MANIFEST.MF

:: Create JAR
echo Creating JAR...
cd out\classes
"%JAVA_HOME%\bin\jar" cfm ..\app\InventorySystem.jar MANIFEST.MF *.class model\*.class view\*.class controller\*.class
cd ..\..

:: Download JDK 17 if not exists
if not exist "jdk-17" (
    echo Downloading JDK 17...
    powershell -Command "& { Invoke-WebRequest -Uri 'https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip' -OutFile 'jdk17.zip' }"
    echo Extracting JDK 17...
    powershell -Command "& { Expand-Archive -Path 'jdk17.zip' -DestinationPath '.' }"
    del jdk17.zip
    ren jdk-17.0.2 jdk-17
)

:: Create executable using JDK 17
echo Creating executable...
"%JAVA_HOME%\bin\jpackage" ^
--type exe ^
--input out/app ^
--dest out ^
--name "InventorySystem" ^
--main-jar InventorySystem.jar ^
--main-class App ^
--module-path "%PATH_TO_FX%" ^
--add-modules javafx.controls,javafx.base,javafx.graphics ^
--win-dir-chooser ^
--win-shortcut ^
--win-menu ^
--vendor "Your Company" ^
--description "Inventory Management System" ^
--app-version 1.0.0 ^
--java-options "--module-path=app/lib" ^
--java-options "--add-modules=javafx.controls,javafx.base,javafx.graphics" ^
--java-options "-Djava.library.path=app/lib/bin;app/lib" ^
--resource-dir out/app/lib ^
--win-console

:: Create a debug batch file in the output directory
echo Creating debug launcher...
(
echo @echo off
echo cd "%%~dp0"
echo set PATH=%%PATH%%;app\lib\bin;app\lib
echo java --module-path app/lib --add-modules javafx.controls,javafx.base,javafx.graphics -Djava.library.path=app/lib/bin;app/lib -jar app/InventorySystem.jar
echo if errorlevel 1 (
echo     echo Application failed to start. Error code: %%errorlevel%%
echo     pause
echo ^)
)> out\InventorySystem-debug.bat

echo Build complete! Check the 'out' directory for the installer.
pause