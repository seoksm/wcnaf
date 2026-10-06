@echo off
setlocal

REM Check if java is installed
where java >nul 2>nul
if errorlevel 1 (
    echo.
    echo [ERROR] Java is not installed or not in PATH.
    pause
    exit /b 1
)

REM Check Java version
for /f "tokens=2 delims==" %%v in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr "java.version[^.]"') do set JAVA_VERSION=%%v

REM Remove quotes if any
set JAVA_VERSION=%JAVA_VERSION:"=%

REM Extract major version
for /f "tokens=1 delims=." %%a in ("%JAVA_VERSION%") do set MAJOR_VERSION=%%a

REM If version starts with 1., extract second part
if "%MAJOR_VERSION%"=="1" (
    for /f "tokens=2 delims=." %%b in ("%JAVA_VERSION%") do set MAJOR_VERSION=%%b
)

if %MAJOR_VERSION% LSS 11 (
    echo.
    echo [ERROR] Java 11 or higher is required. Found version %JAVA_VERSION%.
    pause
    exit /b 1
)

echo Running service-creator.jar...
cd service-creator\program\
java -jar service-creator.jar
