#!/bin/bash
cp /root/verkeer2/jars/verkeer2/VerkeerLib.jar /opt/glassfish4/glassfish/lib/
cp /root/verkeer2/jars/verkeer2/VerkeerLib.jar /opt/glassfish4/glassfish/domains/domain1/lib/applibs/
cd /opt/glassfish4/glassfish/bin/ 
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name Logger --type ejb --deploymentorder 100 --libraries VerkeerLib.jar /root/verkeer2/jars/verkeer2/Logger.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name GeneralDAO --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar /root/verkeer2/jars/verkeer2/GeneralDAO.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name GeneralDAONoDB --type ejb --deploymentorder 110 --libraries VerkeerLib.jar /root/verkeer2/jars/verkeer2/GeneralDAONoDB.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name TrafficDataDAO --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar /root/verkeer2/jars/verkeer2/TrafficDataDAO.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name GoogleMapsSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar /root/verkeer2/jars/verkeer2/GoogleMapsSourceAdapter.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name HereSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar /root/verkeer2/jars/verkeer2/HereSourceAdapter.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name TrafficDataDownloader --type ejb --deploymentorder 120 --libraries VerkeerLib.jar /root/verkeer2/jars/verkeer2/TrafficDataDownloader.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name TimerScheduler --type ejb --deploymentorder 130 --libraries VerkeerLib.jar /root/verkeer2/jars/verkeer2/TimerScheduler.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name TrafficDataDownstreamAnalyser --type ejb --deploymentorder 150 --libraries VerkeerLib.jar /root/verkeer2/jars/verkeer2/TrafficDataDownstreamAnalyser.jar
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt deploy --name RestApi --type war --contextroot api --deploymentorder 160 --libraries VerkeerLib.jar,json-20151123.jar /root/verkeer2/jars/verkeer2/RestApi.war
cd /root/verkeer2/
