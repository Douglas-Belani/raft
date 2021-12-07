#!/bin/bash

echo "Starting application"

source /etc/environment

cd /app
echo "Current directory: ${PWD}"

NOHUP_OUT="nohup.out"

if [ -f NOHUP_OUT ]; then
  echo "Deleting file ${NOHUP_OUT}"
  rm ${NOHUP_OUT}
fi

APP_NAME="rest-api-file-transfer-0.0.1-SNAPSHOT.jar"

nohup java -Dlog4j.configurationFile=./config/logback.xml -jar ./lib/${APP_NAME} &

PID=$(ps -ef | grep "${APP_NAME}" | grep -v grep | awk '{print $2}')

echo "Application started with PID ${PID}"