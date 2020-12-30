@echo off
setlocal enabledelayedexpansion
setlocal

rem # Set bulk home directory
set BULK_HOME=%~dp0..

rem # Set java - use bundled first
if "%PROCESSOR_ARCHITECTURE%" == "AMD64" (
    if exist "%BULK_HOME%\bin\support\win64\jre1.8.0_202" (
	    set JAVA="%BULK_HOME%\bin\support\win64\jre1.8.0_202\bin\java.exe"
	    goto found_java
    )
) else (
    if exist "%BULK_HOME%\bin\support\win32\jre1.8.0_202" (
	    set JAVA="%BULK_HOME%\bin\support\win32\jre1.8.0_202\bin\java.exe"
	    goto found_java
    )
)

if defined JAVA_HOME (
    set JAVA="%JAVA_HOME%\bin\java.exe"
) else (
    for %%I in (java.exe) do set JAVA="%%~$PATH:I"
)

if exist %JAVA% goto found_java

echo Failed to find java - set JAVA_HOME or add java to the PATH 1>&2
timeout /t 10
exit /b 1

:found_java

for /f "delims=" %%x in ('dir /od /b "%BULK_HOME%\lib\*.jar"') do set latestjar=%%x

rem # Set command line arguments
set JVM_COMMAND_LINE=--conf.file "%BULK_HOME%\conf\onboarding.csv"
%JAVA% -jar "%BULK_HOME%\lib\%latestjar%" %JVM_COMMAND_LINE% %*
goto end

:end
endlocal