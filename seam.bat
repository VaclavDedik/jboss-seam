@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set WORKING_DIR=%CD%
set SEAM_DIR=%~dp0
if "%SEAM_DIR:~-1%" == "\" set SEAM_DIR=%SEAM_DIR:~0,-1%
set SEAM_GEN_DIR=%SEAM_DIR%\seam-gen
set COMMAND=%1%
set ARGS=%*

if [%COMMAND%] == [] (goto usage)

if %COMMAND% == help (goto help)

if ["%JAVA_HOME%"] == [] (goto nojava)

if not exist "%JAVA_HOME%\bin\javac.exe" (goto nojdk)

java -cp "%JAVA_HOME%\lib\tools.jar;%SEAM_DIR%\build\lib\ant-launcher.jar;%SEAM_DIR%\build\lib\ant-nodeps.jar;%SEAM_DIR%\build\lib\ant.jar" -Dant.home="%SEAM_DIR%\lib" org.apache.tools.ant.launch.Launcher -buildfile "%SEAM_GEN_DIR%\build.xml" -Dworking.dir=%WORKING_DIR% %ARGS%

goto END_NO_PAUSE

:nojava
echo The JAVA_HOME environment variable is not set
echo Please point it to a valid JDK installation
goto END_NO_PAUSE

:nojdk
echo The JAVA_HOME environment variable should point to a JDK, not a JRE
goto END_NO_PAUSE

:usage
more %SEAM_GEN_DIR%\USAGE
goto END_NO_PAUSE

:help
more %SEAM_GEN_DIR%\README
goto END_NO_PAUSE

:END_NO_PAUSE