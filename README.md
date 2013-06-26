tokens-service-java
===================

The sample tokens microservice written in Java


How to build
============
```bash
mvn clean install
```
(you can also run it in debug from eclipse very nicely: run as -> mvn build... then add the jetty:run goal)


How to run
===========
__With maven__

It declares the jetty plugin, so Usual drill:
```bash
mvn jetty:run
```
__With any java application server__ 

It's a war file, so you can deploy it in your favourite web server
```bash
yadda yadda deploy
```
__As a standalone application__

It's packaged as an executable jar, so you can simply type: 
```bash
java -jar target/api-tokens-1.0.0-SNAPSHOT.war [port]
```
(default port is 8002)
Then point your browser to:
<http://localhost:8002>

It currently contains a very stupid in memory implementation and it also sports a full swagger docs, so you can play with it a bit   


  
.   
(man this wiki syntax is so bad... it makes me cry!!)
