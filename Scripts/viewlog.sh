#!/bin/bash
. ./configuration.sh

tail -f -n 8000 $glassfishInstall/domains/$domainName/logs/server.log 
