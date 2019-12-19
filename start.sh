#!/usr/bin/env sh

set -xe

if [ ! -z $OLIVE_JAVA_LOG4J_PATH ]; then
    SET_JAVA_LOG="-Dlog4j.configurationFile=$OLIVE_JAVA_LOG4J_PATH"
fi

# Fix for monitoring
export JDK_JAVA_OPTIONS="--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED"

exec "java" \
            "-Djava.security.egd=file:/dev/./urandom" \
            ${SET_JAVA_LOG} \
            "-XX:+UseG1GC" \
            "-jar" "./app.jar" \
            "$@"
