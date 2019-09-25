**1. Run zookeeper and kafka server** <br>
zookeeper-server-start.bat zookeeper.properties <br>
kafka-server-start.bat server.properties

**2.  Run Application.**

**3.  Test!.** <br>
kafka-console-producer.bat --broker-list localhost:9092 --topic product <br>
 Enter -> {"name":"james","brand":"test"}"