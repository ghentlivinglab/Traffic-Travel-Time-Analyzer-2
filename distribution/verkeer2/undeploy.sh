#!/bin/bash
. ./configuration.sh

cd $glassfishInstall/bin/ 
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy Logger
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy TwitterHandler
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy GeneralDAO
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy TrafficDataDAO
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy DataProvider
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy GeoJsonProvider
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy Thresholds
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy GoogleMapsSourceAdapter
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy HereSourceAdapter
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy TomTomSourceAdapter
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy TrafficDataDownloader
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy TimerScheduler
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy TrafficDataDownstreamAnalyser
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy RestApi
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy Verkeerscentrum
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy CoyoteSourceAdapter
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy LoginDAO	
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy APIKeyDAO
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy Properties
./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt undeploy WazeSourceAdapter
cd -
