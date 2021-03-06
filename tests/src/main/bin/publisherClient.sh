#!/bin/bash
#  Launcher script for OpenAMQ/JMS test program

BASE_DIRECTORY=$(readlink -f $(dirname $0))

#  Find Java and required JAR files
LAUNCHER_JAR=openamq-jms-tests-${project.version}.jar
if [ -n "$JAVA_HOME" ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA=java
fi
OPENAMQ_JAVA_HOME=
if [ -n "$IBASE" -a -f "$IBASE/java/lib/$LAUNCHER_JAR" ]; then
    OPENAMQ_JAVA_HOME=$IBASE/java/lib
fi
if [ -f "$BASE_DIRECTORY/../jars/$LAUNCHER_JAR" ]; then
    OPENAMQ_JAVA_HOME=$(readlink -f "$BASE_DIRECTORY/../jars/")
fi
if [ ! -f "$OPENAMQ_JAVA_HOME/$LAUNCHER_JAR" ]; then
    cat <<EOM
Could not locate $LAUNCHER_JAR in any of:

\$IBASE/java/lib/$LAUNCHER_JAR
../dist/$LAUNCHER_JAR
./dist/$LAUNCHER_JAR

Please either set \$IBASE to point to the IBASE where you installed 
OpenAMQ/JMS, or run this script from the top-level directory of the
OpenAMQ/JMS distribution.
EOM
    exit 1
fi

CLASSPATH=$(export JARS=""; for file in $(ls $OPENAMQ_JAVA_HOME/*.jar) ; do JARS="${JARS:+$JARS:}$file" ; done; echo $JARS)

HOST=$1
shift
#  Execute the test
exec $JAVA -server -Xmx1024m -Xms1024m -XX:NewSize=300m -cp $CLASSPATH \
      -Damqj.logging.level="INFO" \
      org.openamq.pubsub1.TestPublisher $HOST 5672 "Test1 = abc, Test2 = xyz" 10000
