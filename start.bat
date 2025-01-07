@echo off
echo Starting Inventory Management System...
echo.

:: Run compile.bat and check for errors
call compile.bat
if errorlevel 1 (
    echo.
    echo Failed to compile! Please check the errors above.
    pause
    exit /b 1
)

:: If compilation successful, run the application
echo.
echo Starting application...
echo.
call run.bat

exit /b %errorlevel% 