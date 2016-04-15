#!/bin/bash
cd /opt/glassfish4/glassfish/bin/ 
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy Logger
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy GeneralDAO
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy GeneralDAONoDB
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy TrafficDataDAO
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy GoogleMapsSourceAdapter
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy HereSourceAdapter
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy TrafficDataDownloader
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy TimerScheduler
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy TrafficDataDownstreamAnalyser
./asadmin --user admin --passwordfile /root/verkeer2/config/glassfishpwd.txt undeploy RestApi
cd /root/verkeer2/
