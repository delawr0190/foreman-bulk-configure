#!/bin/bash

BULK_HOME=$(
  cd $(dirname $0)/..
  pwd
)

JAVA=$(command -v java)
if [ -z "$JAVA" ]; then
  if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
  fi

  if [ ! -x "$JAVA" ]; then
    echo "Couldn't find java - set JAVA_HOME or add java to the path"
    exit 1
  fi
fi

$JAVA \
  -jar ${BULK_HOME}/lib/*.jar \
  --conf.file=${BULK_HOME}/conf/on-boarding.csv
