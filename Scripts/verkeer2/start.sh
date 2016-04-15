#!/bin/bash
cd /opt/glassfish4/glassfish/bin/  
cat /dev/null > /opt/glassfish4/glassfish/domains/domain1/logs/server.log
./asadmin start-domain
cat /opt/glassfish4/glassfish/domains/domain1/logs/server.log | grep 'done'
cd /root/verkeer2/
