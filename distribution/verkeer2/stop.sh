#!/bin/bash
. ./configuration.sh

cd $glassfishInstall/bin/  
./asadmin stop-domain $domainName
cd -
