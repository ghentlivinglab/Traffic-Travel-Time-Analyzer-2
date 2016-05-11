#!/bin/bash
. ./configuration.sh

#create domain an apply fixes, adjust memory size and set log params
cd $glassfishInstall/bin;
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-domain $domainName
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation start-domain $domainName
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-jvm-options '-Dorg.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton':'-Duser.timezone=Europe/Brussels':'-XX\:MaxPermSize='$MaxPermSize:'-Xmx'${MaxMemorySize}:'-XX\:+UseG1GC':'-XX\:+UnlockExperimentalVMOptions'
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-jvm-options '-Dorg.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton':'-Duser.timezone=Europe/Brussels':'-XX\:MaxPermSize='$MaxPermSize:'-Xmx'${MaxMemorySize}:'-XX\:+UseG1GC':'-XX\:+UnlockExperimentalVMOptions'
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation set server.ejb-container.property.disable-nonportable-jndi-names="true"
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=$logSize
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.maxHistoryFiles=$maxLogFiles
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation stop-domain $domainName
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-service $domainName
cd -

#Copy libraries to glassfish library folders
cp -rf $verkeer2Install/jars/lib/* $glassfishInstall/lib/
cp -rf $verkeer2Install/jars/lib/* $glassfishInstall/domains/domain1/lib/
cp -rf $verkeer2Install/jars/lib/* $glassfishInstall/domains/domain1/lib/applibs
unlink logs
ln -s $glassfishInstall/domains/domain1/logs logs

#create jdbc and jndi connections
cd $glassfishInstall/bin;
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation start-domain $domainName

./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-jdbc-resource jdbc/verkeer/general
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-jdbc-connection-pool generalMySql
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/Beans
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/DataProvider
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/GeoJsonProvider
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/ThresholdManager
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/TimerScheduler
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/WebSettings
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/Twitter
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/ThresholdHandlers
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/Logger
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/SourceAdaptersKeys
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/SourceAdapters
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/TrafficDataDownstreamAnalyser
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/TDDAnalyser
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/CoyoteMapping
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/WazeMapping
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation delete-custom-resource resources/properties/Properties

./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-jdbc-connection-pool --datasourceclassname $datasourceClassName --restype javax.sql.DataSource --property DatabaseName=$datasourceDatabaseName:User=$datasourceUser:Password=$datasourcePassword:Url=$datasourceUrl":URL=$datasourceUrl":PortNumber=$datasourcePortNumber generalMySql
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-jdbc-resource --connectionpoolid generalMySql jdbc/verkeer/general
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/Beans.properties:propertyLocation=$verkeer2Install/config/Beans.properties resources/properties/Beans
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/DataProvider.properties:propertyLocation=$verkeer2Install/config/DataProvider.properties resources/properties/DataProvider
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/GeoJsonProvider.properties:propertyLocation=$verkeer2Install/config/GeoJsonProvider.properties resources/properties/GeoJsonProvider
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/ThresholdManager.properties:propertyLocation=$verkeer2Install/config/ThresholdManager.properties resources/properties/ThresholdManager
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/TimerScheduler.properties:propertyLocation=$verkeer2Install/config/TimerScheduler.properties resources/properties/TimerScheduler
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/WebSettings.properties:propertyLocation=$verkeer2Install/config/WebSettings.properties resources/properties/WebSettings
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/Twitter.properties:propertyLocation=$verkeer2Install/config/Twitter.properties resources/properties/Twitter
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/ThresholdHandlers.properties:propertyLocation=$verkeer2Install/config/ThresholdHandlers.properties resources/properties/ThresholdHandlers
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/Logger.properties:propertyLocation=$verkeer2Install/config/Logger.properties resources/properties/Logger
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/SourceAdaptersKeys.properties:propertyLocation=$verkeer2Install/config/SourceAdaptersKeys.properties resources/properties/SourceAdaptersKeys
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/SourceAdapters.properties:propertyLocation=$verkeer2Install/config/SourceAdapters.properties resources/properties/SourceAdapters
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/TrafficDataDownstreamAnalyser.properties:propertyLocation=$verkeer2Install/config/TrafficDataDownstreamAnalyser.properties resources/properties/TrafficDataDownstreamAnalyser
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/TrafficDataDownstreamAnalyser.properties:propertyLocation=$verkeer2Install/config/TrafficDataDownstreamAnalyser.properties resources/properties/TDDAnalyser
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/CoyoteMapping.properties:propertyLocation=$verkeer2Install/config/CoyoteMapping.properties resources/properties/CoyoteMapping
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/WazeMapping.properties:propertyLocation=$verkeer2Install/config/WazeMapping.properties resources/properties/WazeMapping
./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property org.glassfish.resources.custom.factory.PropertiesFactory.fileName=$verkeer2Install/config/Properties.properties:propertyLocation=$verkeer2Install/config/Properties.properties resources/properties/Properties

./asadmin --user $glassfishUser --passwordfile $verkeer2Install/$glassfishPasswordLocation stop-domain $domainName
cd -

./cycle.sh