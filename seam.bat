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

if %SEAMTASK% == setup ( ant setup -buildfile=%SEAMGENDIR%\build.xml ) else (goto new-project)

:new-project
if %SEAMTASK% == new-project ( ant new-project -buildfile=%SEAMGENDIR%\build.xml -Dproject.name=%PROJECTNAME% ) else (goto explode)

:explode
if %SEAMTASK% == explode ( ant explode -buildfile=%SEAMGENDIR%\build.xml ) else (goto unexplode)

:unexplode
if %SEAMTASK% == unexplode ( ant unexplode -buildfile=%SEAMGENDIR%\build.xml ) else (goto deploy)

:deploy
if %SEAMTASK% == deploy ( ant deploy -buildfile=%SEAMGENDIR%\build.xml ) else (goto undeploy)

:undeploy
if %SEAMTASK% == undeploy ( ant undeploy -buildfile=%SEAMGENDIR%\build.xml ) else (goto restart)

:restart
if %SEAMTASK% == restart ( ant restart -buildfile=%SEAMGENDIR%\build.xml ) else (goto new-action)

:new-action
if %SEAMTASK% == new-action ( ant new-action -buildfile=%SEAMGENDIR%\build.xml ) else (goto new-form)

:new-form
if %SEAMTASK% == new-form ( ant new-form -buildfile=%SEAMGENDIR%\build.xml ) else (goto new-conversation)

:new-conversation
if %SEAMTASK% == new-conversation ( ant new-conversation -buildfile=%SEAMGENDIR%\build.xml ) else (goto new-entity)

:new-entity
if %SEAMTASK% == new-entity ( ant new-entity -buildfile=%SEAMGENDIR%\build.xml ) else (goto generate-entities)

:generate-entities
if %SEAMTASK% == generate-entities ( ant generate-entities -buildfile=%SEAMGENDIR%\build.xml ) else (goto help)

:help
if %SEAMTASK% == help ( more %SEAMGENDIR%\README ) else (goto usage)

:usage
more %SEAMGENDIR%\USAGE

goto END_NO_PAUSE

:END_NO_PAUSE
