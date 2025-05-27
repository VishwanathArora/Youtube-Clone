#!/usr/bin/env sh
set -e

if [ -z "$JAVA_HOME" ]; then
  echo "JAVA_HOME is not set"
  exit 1
fi

DIR=\$(dirname "\$0")
"\$DIR/gradle/gradlew" "\$@"