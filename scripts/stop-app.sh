#!/bin/bash

echo "Stopping application"

APP_NAME="rest-api-file-transfer-0.0.1-SNAPSHOT.jar"

PID=$(ps -ef | grep "${APP_NAME}" | grep -v grep | awk '{print $2}')

kill -9 "${PID}"

echo "Application stopped"

exit 0