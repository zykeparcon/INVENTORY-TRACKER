@echo off
setlocal

:: Set the path to JavaFX native libraries
set PATH=%PATH%;%CD%\lib\bin

:: Run the application with proper module path and native library path
java --module-path lib --add-modules javafx.controls,javafx.base,javafx.graphics ^
     -Djavafx.verbose=true ^
     -Djava.library.path=lib\bin;lib ^
     -jar out/app/InventorySystem.jar

pause 