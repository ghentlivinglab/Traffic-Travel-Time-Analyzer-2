#!/bin/bash
#file locations
glassfishInstall=/opt/glassfish4/glassfish
verkeer2Install=/root/verkeer2

#glassfish login
glassfishUser=admin
glassfishPasswordLocation=config/glassfishpwd.txt

#glassfish settings
#not tested. dont change the domain name if you don't have to
domainName=domain1
MaxPermSize=512m
MaxMemorySize=2048m
logSize=10000000
maxLogFiles=30

#database connection
datasourceClassName=com.mysql.jdbc.jdbc2.optional.MysqlDataSource
datasourceUser=verkeer
datasourceDatabaseName=verkeer2
datasourcePassword=vop2016
#dont forget to 3x \ : characters
datasourceUrl=jdbc\\\:mysql\\\://127.0.0.1\\\:3306/verkeer2
datasourcePortNumber=3306

#context roots
verkeerscentrumCR=
restapiCR=api