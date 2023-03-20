#!/bin/sh

SERVICE_NAME=HYPHEN_FEP
DISPLAY_NAME=HYPHEN_FEP
DESCRIPTION="Hyphen fep program"
INSTALL_HOME=$(pwd)
CLASSPATH=$INSTALL_HOME/lib/*
BINARYPATH=$INSTALL_HOME/classes
SOURCE_PATH=$INSTALL_HOME/src
MAIN_CLASS=im.hyphen.firm_bypass.FirmBypass
CONFIG_FILE=$INSTALL_HOME/conf/config.ini
SYSTEM_FILE=$INSTALL_HOME/conf/system.ini

if [[ $(uname -m) == "64" ]]; then
  OPERATING_SYSTEM=64
else
  OPERATING_SYSTEM=32
fi



#Prompt the user for JAVA_HOME if it is not defined or does not exist
while [ ! -d "$JAVA_HOME" ]; do
	echo "Please enter the path to your Java installation(ex. /usr/java8/)"
	read JAVA_HOME
done

#JAVA_HOME=${JAVA_HOME:-/usr/java}

echo "OPERATING_SYSTEM is $OPERATING_SYSTEM bit"
echo "INSTALL_HOME is set to: $INSTALL_HOME"
echo "CLASSPATH is set to: $CLASSPATH"
echo "JAVA_HOME is set to: $JAVA_HOME"

# Compile the Java source code
"$JAVA_HOME/bin/javac" -encoding UTF8 -cp "$CLASSPATH" -d classes \
  "$SOURCE_PATH/im/hyphen/crypto/"*.java \
  "$SOURCE_PATH/im/hyphen/firm_bypass/"*.java \
  "$SOURCE_PATH/im/hyphen/msgVO/"*.java \
  "$SOURCE_PATH/im/hyphen/receiver/"*.java \
  "$SOURCE_PATH/im/hyphen/sender/"*.java \
  "$SOURCE_PATH/im/hyphen/util/"*.java

# Build the command to start your Java program
CMD="$JAVA_HOME/bin/java -cp $BINARYPATH:$CLASSPATH $MAIN_CLASS $CONFIG_FILE $SYSTEM_FILE"

# Start the service using nohup
nohup $CMD > $INSTALL_HOME/stdout.log 2> $INSTALL_HOME/stderr.log &

echo "Service $SERVICE_NAME started successfully."
