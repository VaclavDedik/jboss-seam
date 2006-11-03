@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=seam.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

rem Read all command line arguments
set SEAMTASK=%1%
set PROJECTNAME=%2%
set TASKINPUT=%3%
set TASKINPUT2=%4%

if "%SEAMTASK%" == "" more USAGE

if "%SEAMTASK%" == "set-properties" ant -buildfile=build-project-setup.xml

if "%SEAMTASK%" == "new-project" ant new-project -Dproject.name=%PROJECTNAME%

if "%SEAMTASK%" == "new-wtp-project" ant new-wtp-project -Dproject.name=%PROJECTNAME%

if "%SEAMTASK%" == "deploy-project" ant deploy-project -Dproject.name=%PROJECTNAME% 
	
if "%SEAMTASK%" == "new-action" ant new-stateless-action -Dproject.name=%PROJECTNAME% -Daction.name=%TASKINPUT% -Dpage.name=%TASKINPUT2%

if "%SEAMTASK%" == "new-form" ant new-stateful-action -Dproject.name=%PROJECTNAME% -Daction.name=%TASKINPUT% -Dpage.name=%TASKINPUT2%

if "%SEAMTASK%" == "new-conversation" ant new-conversation -Dproject.name=%PROJECTNAME% -Daction.name=%TASKINPUT% -Dpage.name=%TASKINPUT2%

if "%SEAMTASK%" == "new-entity" ant new-entity -Dproject.name=%PROJECTNAME% -Daction.name=%TASKINPUT%

if "%SEAMTASK%" == "new-mdb" ant new-mdb -Dproject.name=%PROJECTNAME% -Daction.name=%TASKINPUT%

if "%SEAMTASK%" == "help" more README

goto END_NO_PAUSE

:END_NO_PAUSE
