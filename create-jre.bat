@echo off
setlocal enabledelayedexpansion

:: Create minimal JRE
"%JAVA_HOME%\bin\jlink" ^
  --module-path "%JAVA_HOME%\jmods" ^
  --add-modules java.base,java.desktop,javafx.controls,javafx.base,javafx.graphics ^
  --output jre ^
  --strip-debug ^
  --compress 2 ^
  --no-header-files ^
  --no-man-pages

echo JRE created successfully!
pause 