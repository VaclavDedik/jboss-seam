@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=seam.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

rem Read all command line arguments
set SEAMTASK=%1%
set TASKINPUT=%2%
set TASKINPUT2=%3%
set TASKINPUT3=%4%

cd seam-gen

if "%SEAMTASK%" == "" more USAGE

if "%SEAMTASK%" == "setup" ant setup

if "%SEAMTASK%" == "new-project" ant new-project -Dproject.name=%TASKINPUT%

if "%SEAMTASK%" == "new-wtp-project" ant new-wtp-project -Dproject.name=%TASKINPUT%

if "%SEAMTASK%" == "deploy" ant deploy
	
if "%SEAMTASK%" == "deploy" ant undeploy
	
if "%SEAMTASK%" == "new-action" ant new-stateless-action -Daction.name=%TASKINPUT% -Dpage.name="%TASKINPUT2%"

if "%SEAMTASK%" == "new-form" ant new-stateful-action -Daction.name=%TASKINPUT% -Dpage.name="%TASKINPUT2%"

if "%SEAMTASK%" == "new-conversation" ant new-conversation -Daction.name=%TASKINPUT% -Dpage.name="%TASKINPUT2%"

if "%SEAMTASK%" == "new-entity" ant new-entity -Daction.name=%TASKINPUT% -Dpage.name="%TASKINPUT2%" -DmasterPage.name="%TASKINPUT3%"

if "%SEAMTASK%" == "new-mdb" ant new-mdb -Daction.name=%TASKINPUT%

if "%SEAMTASK%" == "help" more README

goto END_NO_PAUSE

:END_NO_PAUSE

cd ..