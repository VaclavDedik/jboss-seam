@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT" setlocal

set WORKING_DIR=%CD%
if "%WORKING_DIR:~-1%" == "\" (
    set WORKING_DIR=%WORKING_DIR:~0,-1%
)

if ["%SEAM_HOME%"] == [""] (
    set SEAM_HOME=%~dp0
) else (
    if not exist "%SEAM_HOME\seam" (goto noseam)
)
if "%SEAM_HOME:~-1%" == "\" (
    set SEAM_HOME=%SEAM_HOME:~0,-1%
)

set SEAM_GEN_DIR=%SEAM_HOME%\seam-gen
set COMMAND=%1%

echo Location of seam script: %SEAM_HOME%
echo seam-gen template folder: %SEAM_GEN_DIR%

if [%COMMAND%] == [] (goto usage)

if %COMMAND% == help (goto help)

if ["%JAVA_HOME%"] == [""] (goto nojava)

if not exist "%JAVA_HOME%\bin\javac.exe" (goto nojdk)

java -cp "%JAVA_HOME%\lib\tools.jar;%SEAM_HOME%\build\lib\ant-launcher.jar;%SEAM_HOME%\build\lib\ant-nodeps.jar;%SEAM_HOME%\build\lib\ant.jar" -Dant.home="%SEAM_HOME%\lib" org.apache.tools.ant.launch.Launcher -buildfile "%SEAM_GEN_DIR%\build.xml" -Dworking.dir="%WORKING_DIR%" %*

goto END_NO_PAUSE

:nojava
echo The JAVA_HOME environment variable is not set
echo Please point it to a JDK installation
goto END_NO_PAUSE

:nojdk
echo The JAVA_HOME environment variable should point to a JDK, not a JRE
goto END_NO_PAUSE

:noseam
echo The SEAM_HOME environment variable should point to a Seam distribution
goto END_NO_PAUSE

:usage
more %SEAM_GEN_DIR%\USAGE
goto END_NO_PAUSE

:help
more %SEAM_GEN_DIR%\README
goto END_NO_PAUSE

:END_NO_PAUSE
