#!/bin/sh

JMX_PROPS="-Dcom.sun.management.jmxremote.port=33333 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.net.preferIPv4Stack=true"
JPDA_PROPS="-agentlib:jdwp=transport=dt_socket,server=y,address=32333,suspend=n"

nohup java -server $JMX_PROPS $JPDA_PROPS -Dfile.encoding=UTF8 -cp ./*:config:lib/* -Xmx1024m -Xms572m com.serge.pingpong.PingPongMain 1> PingPong.out 2>&1 &

pid=$!
echo "PID = $pid"
echo $pid > last.pid
