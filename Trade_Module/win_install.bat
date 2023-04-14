@echo off

set SERVICE_NAME=HYPHEN_FEP
set DISPLAY_NAME=HYPHEN_FEP
set DESCRIPTION=Hyphen fep program
set INSTALL_HOME="%CD%
set CLASSPATH=%INSTALL_HOME%\lib\*"
set BINARYPATH=%INSTALL_HOME%\classes"
set SOURCE_PATH=%INSTALL_HOME%\src
set MAIN_CLASS=im.hyphen.firm_bypass.FirmBypass
set CONFIG_FILE=\%INSTALL_HOME%\conf\config.ini\"
set SYSTEM_FILE=\%INSTALL_HOME%\conf\system.ini\"


if "%PROCESSOR_ARCHITECTURE%"=="AMD64" (
  set OPERATING_SYSTEM=64
) else (
  set OPERATING_SYSTEM=32
)

set NSSM_PATH=%INSTALL_HOME%\nssm\nssm_%OPERATING_SYSTEM%"






REM Check if JAVA_HOME is set
if "%JAVA_HOME%" == "" (
  echo JAVA_HOME is not set.
  set /p JAVA_HOME=Enter the path to your Java installation:
)


echo OPERATING_SYSTEM is %OPERATING_SYSTEM% bit
echo INSTALL_HOME is set to: %INSTALL_HOME%
echo NSSM_PATH is set to : %NSSM_PATH%
echo CLASSPATH is set to : %CLASSPATH%
echo JAVA_HOME is set to : %JAVA_HOME%



REM Compile the Java source code
cd %INSTALL_HOME%
"%JAVA_HOME%\bin\javac.exe" -encoding UTF-8 -cp %CLASSPATH% -d classes -sourcepath ./src ./src/im/hyphen/crypto/*.java  ./src/im/hyphen/firm_bypass/*.java ./src/im/hyphen/msgVO/*.java ./src/im/hyphen/receiver/*.java ./src/im/hyphen/sender/*.java ./src/im/hyphen/util/*.java
set COM_CMD="%JAVA_HOME%\bin\javac.exe" -encoding UTF-8 -cp %CLASSPATH% -d classes -sourcepath ./src ./src/im/hyphen/crypto/*.java  ./src/im/hyphen/firm_bypass/*.java ./src/im/hyphen/msgVO/*.java ./src/im/hyphen/receiver/*.java ./src/im/hyphen/sender/*.java ./src/im/hyphen/util/*.java
echo %COM_CMD%



REM Build the command to start your Java program



set CMD="%JAVA_HOME%\bin\java.exe" -cp \%BINARYPATH%;%CLASSPATH% %MAIN_CLASS% %CONFIG_FILE% %SYSTEM_FILE%
echo %CMD%

REM Install the service using NSSM
%NSSM_PATH% install %SERVICE_NAME% %CMD%


%NSSM_PATH% set %SERVICE_NAME% DisplayName "%DISPLAY_NAME%"
%NSSM_PATH% set %SERVICE_NAME% Description "%DESCRIPTION%"
%NSSM_PATH% set %SERVICE_NAME% Start SERVICE_AUTO_START
%NSSM_PATH% set %SERVICE_NAME% AppStdout %INSTALL_HOME%\stdout.log
%NSSM_PATH% set %SERVICE_NAME% AppStderr %INSTALL_HOME%\stderr.log

REM Start the service
net start %SERVICE_NAME%