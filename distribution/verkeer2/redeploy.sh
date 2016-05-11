#!/bin/bash
. ./configuration.sh

cp $verkeer2Install/jars/verkeer2/VerkeerLib.jar $glassfishInstall/lib/
cp $verkeer2Install/jars/verkeer2/VerkeerLibToJson.jar $glassfishInstall/lib/

cp $verkeer2Install/jars/verkeer2/VerkeerLib.jar $glassfishInstall/domains/$domainName/lib/applibs/
cp $verkeer2Install/jars/verkeer2/VerkeerLibToJson.jar $glassfishInstall/domains/$domainName/lib/applibs/

cd $glassfishInstall/bin/ 

if [[ "$#" -eq "0" ]]
then
	printf "No arguments passed.\nDeploying all modules.\n"; 
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Properties --type ejb --deploymentorder 95 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/Properties.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Logger --type ejb --deploymentorder 100 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/Logger.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TwitterHandler --type ejb --deploymentorder 105 --libraries VerkeerLib.jar,commons-codec-1.10.jar,httpcore-4.4.4.jar,httpclient-4.5.2.jar,commons-logging-1.2.jar,httpmime-4.5.2.jar,fluent-hc-4.5.2.jar $verkeer2Install/jars/verkeer2/TwitterHandler.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name GeneralDAO --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/GeneralDAO.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TrafficDataDAO --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/TrafficDataDAO.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name DataProvider --type ejb --deploymentorder 110 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/DataProvider.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name GeoJsonProvider --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/GeoJsonProvider.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Thresholds --type ejb --deploymentorder 110 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/Thresholds.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name GoogleMapsSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/GoogleMapsSourceAdapter.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name HereSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/HereSourceAdapter.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TomTomSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/TomTomSourceAdapter.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name CoyoteSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar,jsoup-1.8.3.jar $verkeer2Install/jars/verkeer2/CoyoteSourceAdapter.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name WazeSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar,jsoup-1.8.3.jar $verkeer2Install/jars/verkeer2/WazeSourceAdapter.jar	
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TrafficDataDownloader --type ejb --deploymentorder 120 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/TrafficDataDownloader.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TimerScheduler --type ejb --deploymentorder 130 --libraries VerkeerLib.jar,commons-net-3.4.jar $verkeer2Install/jars/verkeer2/TimerScheduler.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TrafficDataDownstreamAnalyser --type ejb --deploymentorder 150 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/TrafficDataDownstreamAnalyser.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name APIKeyDAO --type ejb --deploymentorder 150 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/APIKeyDAO.jar
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name LoginDAO --type ejb --deploymentorder 155 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/LoginDAO.jar	
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name RestApi --type war --contextroot "$restapiCR" --deploymentorder 160 --libraries VerkeerLib.jar,json-20151123.jar,VerkeerLibToJson.jar,json-lib-2.4-jdk15.jar,commons-beanutils-1.9.2.jar,commons-collections4-4.1.jar,commons-lang3-3.4.jar,commons-logging-1.2.jar,ezmorph-1.0.6.jar,commons-lang-2.6.jar,commons-collections-3.2.2.jar,xom-1.2.10.jar $verkeer2Install/jars/verkeer2/RestApi.war
	./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Verkeerscentrum --type war --contextroot "$verkeerscentrumCR" --deploymentorder 170 --libraries VerkeerLib.jar,json-20151123.jar,prettyfaces-jsf2-3.3.3.jar,VerkeerLibToJson.jar $verkeer2Install/jars/verkeer2/Verkeerscentrum.war
	exit;
fi

case "$1" in
	"WazeSourceAdapter")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name WazeSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar,jsoup-1.8.3.jar $verkeer2Install/jars/verkeer2/WazeSourceAdapter.jar;;
	"Properties")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Properties --type ejb --deploymentorder 95 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/Properties.jar;;
	"APIKeyDAO")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name APIKeyDAO --type ejb --deploymentorder 150 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/APIKeyDAO.jar;;
	"LoginDAO")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name LoginDAO --type ejb --deploymentorder 155 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/LoginDAO.jar;;
	"CoyoteSourceAdapter")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name CoyoteSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar,jsoup-1.8.3.jar $verkeer2Install/jars/verkeer2/CoyoteSourceAdapter.jar;;
	"Logger")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Logger --type ejb --deploymentorder 100 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/Logger.jar;;
	"TwitterHandler")
		printf "Module %s found\n" "$1";		
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TwitterHandler --type ejb --deploymentorder 105 --libraries VerkeerLib.jar,commons-codec-1.10.jar,httpcore-4.4.4.jar,httpclient-4.5.2.jar,commons-logging-1.2.jar,httpmime-4.5.2.jar,fluent-hc-4.5.2.jar $verkeer2Install/jars/verkeer2/TwitterHandler.jar;;
	"GeneralDAO")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name GeneralDAO --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/GeneralDAO.jar;;
	"TrafficDataDAO")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TrafficDataDAO --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,mysql-connector-java-5.1.38-bin.jar $verkeer2Install/jars/verkeer2/TrafficDataDAO.jar;;
	"DataProvider")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name DataProvider --type ejb --deploymentorder 110 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/DataProvider.jar;;
	"GeoJsonProvider")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name GeoJsonProvider --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/GeoJsonProvider.jar;;
	"Thresholds")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Thresholds --type ejb --deploymentorder 110 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/Thresholds.jar;;
	"GoogleMapsSourceAdapter")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name GoogleMapsSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/GoogleMapsSourceAdapter.jar;;
	"HereSourceAdapter")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name HereSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/HereSourceAdapter.jar;;
	"TomTomSourceAdapter")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TomTomSourceAdapter --type ejb --deploymentorder 110 --libraries VerkeerLib.jar,json-20151123.jar $verkeer2Install/jars/verkeer2/TomTomSourceAdapter.jar;;
	"TrafficDataDownloader")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TrafficDataDownloader --type ejb --deploymentorder 120 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/TrafficDataDownloader.jar;;
	"TimerScheduler")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TimerScheduler --type ejb --deploymentorder 130 --libraries VerkeerLib.jar,commons-net-3.4.jar $verkeer2Install/jars/verkeer2/TimerScheduler.jar;;
	"TrafficDataDownstreamAnalyser")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name TrafficDataDownstreamAnalyser --type ejb --deploymentorder 150 --libraries VerkeerLib.jar $verkeer2Install/jars/verkeer2/TrafficDataDownstreamAnalyser.jar;;
	"RestApi")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name RestApi --type war --contextroot "$restapiCR" --deploymentorder 160 --libraries VerkeerLib.jar,json-20151123.jar,VerkeerLibToJson.jar,json-lib-2.4-jdk15.jar,commons-beanutils-1.9.2.jar,commons-collections4-4.1.jar,commons-lang3-3.4.jar,commons-logging-1.2.jar,ezmorph-1.0.6.jar,commons-lang-2.6.jar,commons-collections-3.2.2.jar,xom-1.2.10.jar $verkeer2Install/jars/verkeer2/RestApi.war;;
	"Verkeerscentrum")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name Verkeerscentrum --type war --contextroot "$verkeerscentrumCR" --deploymentorder 170 --libraries VerkeerLib.jar,json-20151123.jar,prettyfaces-jsf2-3.3.3.jar,VerkeerLibToJson.jar $verkeer2Install/jars/verkeer2/Verkeerscentrum.war;;
	"LeafLetTest")
		printf "Module %s found\n" "$1";
		./asadmin --user admin --passwordfile $verkeer2Install/config/glassfishpwd.txt redeploy --name LeafLetTest --type war --contextroot leaf --deploymentorder 160 --libraries VerkeerLib.jar,json-20151123.jar,VerkeerLibToJson.jar $verkeer2Install/jars/verkeer2/LeafLetTest.war;;
	*)
		printf "Module not found\n" "$1";;
esac

cd -
