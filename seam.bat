@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\

if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=seam.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

set SEAMGENDIR=%DIRNAME%\seam-gen

rem Read all command line arguments
set SEAMTASK=%1%
set PROJECTNAME=%2%

if %SEAMTASK% == setup ant setup -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == new-project ant new-project -buildfile=%SEAMGENDIR%\build.xml -Dproject.name=%PROJECTNAME%

if %SEAMTASK% == explode ant explode -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == unexplode ant unexplode -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == deploy ant deploy -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == undeploy ant undeploy -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == restart ant restart -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == new-action ant new-action -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == new-form ant new-form -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == new-conversation ant new-conversation -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == new-entity ant new-entity -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == generate-entities ant generate-entities -buildfile=%SEAMGENDIR%\build.xml

if %SEAMTASK% == help more %SEAMGENDIR%\README

goto END_NO_PAUSE

:END_NO_PAUSE
