@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\

if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=seam.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

set SEAMGENDIR=%DIRNAME%\seam-gen

set SEAMTASK=%1%
set PROJECTNAME=%2%
set ARGS=%ARGS% %*

if [%1] == [] (goto usage)

:help
if %SEAMTASK% == help ( goto help ) else (goto new-project)

:new-project
if %SEAMTASK% == new-project ( ant new-project -buildfile=%SEAMGENDIR%\build.xml -Dproject.name=%PROJECTNAME% ) else (goto other-tasks)

:other-tasks
ant %ARGS% -buildfile=%SEAMGENDIR%\build.xml 

:usage
more %SEAMGENDIR%\USAGE

:help
more %SEAMGENDIR%\README

goto END_NO_PAUSE

:END_NO_PAUSE
