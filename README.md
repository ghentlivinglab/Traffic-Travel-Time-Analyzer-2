# verkeer-2

## Installeren project

### Systeemeisen

Systeemvereisten:

* 1GB memory minimum (2GB memory recommended)
* 500MB disk space minimum (1GB disk space recommended)
* Supported platforms: Solaris 10, OpenSolaris 2009, Red hat enterprise linux 4+, Ubuntu linux 8+,Windows xp SP3+, Mac OS X 10.5+
* JDK 7 minimum

Software:

* GlassFish Server Open Source Edition 4.1 (build 13) - Java ee container voor de werkelijke applicatie
* MySql server (Project tested on Linux Ubuntu with '5.5.47-0ubuntu0.14.04.1 (Ubuntu) mariadb' ) - Database voor opslag data 
* (optioneel) nginx - Instellen van forwarding naar http / https poorten van de bovenstaande applicaties
 
_Opmerking: Elk van deze onderdelen kunnen op andere systemen draaien om een enkel systeem te ontlasten._

Dit project werd getest met het systeem [hier beschreven](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Hardware).

### Instellen systeem

#### Setup/Installatiemap

Dit project is een modulair systeem bestaande uit 10tallen verschillende java enterprise beans. Om deze allen aan te bieden in 1 geheel , alsook het management hiervan is in de [distribution](https://github.ugent.be/iii-vop2016/) map een verzameling aangelegd. In het vervolg van deze tekst zal hiernaar vermeld worden als '_installatiemap_' of '_setupmap_'. 

De bash shell scripts aanwezig in deze map kunnen gebruikt worden indien het project op een linuxdistributie is geintalleerd. Hierbij dient wel op voorhand de '__configuration.sh__' te worden aangepast naar het systeem waarop deze installatie is gedeployed. Vergeet hierbij vooral niet bij '_glassfishInstall_' naar de installatielocatie van glassfish te verwijzen en bij '_verkeer2Install_' naar het path van de installatiemap te verwijzen.

#### Database
_De MySql database dient reeds gestart te zijn alvorens dit aan te vatten. Afhankelijk van de geistalleerde versie en besturingssysteem kan het start-stop commando verschillen. Contacteer uw systeemadministrator voor meer info en login gegevens._

_Enkele voorbeelden voor linux: systemctl start mariadb.service, systemctl start mysql, /etc/init.d/mariadb.service start, /etc/init.d/mysql start_

Login op de MySql service.

_Onder linux: mysql --user=user_name --password=your_password_

De database dient gecreerd te worden samen met een gebruiker die door de applicatie zal worden gebruikt. 

> Dit script is aanwezig in de setup package onder de naam '_setup.sql_' en kan via fileinput uitgevoerd worden in de MySql service. _Onder linux: mysql --user=user_name --password=your_password_ < setup.sql_
 

Voor een lokale database met gebruikersaccount '_verkeer_' en paswoord '_vop2016_' volstaan volgende commando's:
```sql
CREATE DATABASE verkeer2;
USE verkeer2;
CREATE USER 'verkeer'@'localhost' IDENTIFIED BY 'vop2016';
GRANT ALL ON verkeer2.* TO 'verkeer'@'localhost';
```

#### Glassfish

Eens de database is ingesteld kunnen alle resources voor glassfish worden aangemaakt. Hieronder staat omschreven welke acties dienen uitgevoerd te worden.

> Onderstaand proces is geautomatiseerd voor linux in het bash bestand '_setup.sh_ terug te vinden in het setup package.'

Login op de admin console interface. Consulteer uw administrator om het poortnummer en logingegevens te verkrijgen. Voor de default poort bevindt zich de interface op http://server_adres:4848

* Voer allereerst alle [fixes](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Glassfish---Fixes) door in Glassfish
* Voeg de [externe libraries](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Externe-libraries) toe aan Glassfish
* Herstart Glassfish
* Voeg [JNDI resources](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Glassfish-Resources) toe
* Voeg [JDBC connectie](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Glassfish---JDBC) toe
 
Afhankelijk van een lokale testinstallatie aan netbeans of een server dient een iets ander stappenplan te worden gevolgd:

Lokale server
* Verifieer dat de [Beans](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/Beans.properties) en [SourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/SourceAdaptors.properties) property-bestanden de applicatienaam voor hun JNDI naam hebben staan. Deze moeten voldoen aan volgend formaat:  java:global/ApplicatieNaam/ContainerNaam/BeanNaam!remoteOrLocalInterface. voorbeeld= DataProvider=java:global/Verkeer2/DataProvider/DataProvider!iii.vop2016.verkeer2.ejb.dataprovider.DataProviderLocal

Externe server (hier wordt iedere bean apart gedeployed)
* Verifieer dat de [Beans](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/Beans.properties) en [SourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/SourceAdaptors.properties) property-bestanden de applicatienaam niet voor hun JNDI naam hebben staan. Deze moeten voldoen aan volgend formaat:  java:global/ContainerNaam/BeanNaam!remoteOrLocalInterface. voorbeeld= DataProvider=java:global/DataProvider/DataProvider!iii.vop2016.verkeer2.ejb.dataprovider.DataProviderLocal


***

***
Verslag na sprint 2: __[Document](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Analyse/AnalyseDocumentSprint2.pdf)__
## Structuur repository
__[Root/](https://github.ugent.be/iii-vop2016/verkeer-2)__

Deze map bevat de logboeken, dit README-bestand en GNU license.

__[Analyse/](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Analyse)__

De map bevat alle analysedocumenten van het project.

__[Realisatie/](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie)__

De map bevat alle sourcecode van het project, alsook de property-bestanden gebruikt tijdens deployment.

Iedere directory is een aparte EJB (Enterprise Java Bean). VerkeersLib stelt de gemeenschappelijke library van het project voor.

De map 'lib/' bevat de gebruikte externe libraries.

***

## Structuur project

### Business logic

__[Logger](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/Logger)__

Deze bean start mee op met de server en creëert een datasink voor logging naar een bestand. (log.txt)
````
Package name: iii.vop2016.verkeer2.ejb.logger

EJB Bean: Logger
  Session Beans: Logger
  Library interface: LoggerRemote
````

__[TimerScheduler](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TimerScheduler)__

Deze bean start mee op met de server en creëert een timer via Java EE TimerServices die volgens een patroon, gedefinieerd in properties file '[TimerScheduler](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/TimerScheduler.properties)', de download van data triggert.
````
Package name: iii.vop2016.verkeer2.ejb.timer

EJB Bean: TimerScheduler
  Session Beans: TimerScheduler
  Library interface: ITimer
````

__[TrafficDataDownloader](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDownloader)__

De downloader staat in voor de connectie tussen de providers en de databases. Deze klasse, met slechts één enkele methode, wordt getriggerd vanuit de TimerServices, vraagt data over de routes en vraagt de verkeerssituatie op aan de providers. Deze data wordt vervolgens naar de trafficDataDAO voor dataopslag gepushed via de DownstreamAnalyser, die de desbetreffende data zal controleren en eventuele meldingen zal genereren. De providers worden beheerd door de extra klasse SourceManager.
````
Package name: iii.vop2016.verkeer2.ejb.datadownloader

EJB Bean: TrafficDataDownloader
  Session Beans: TrafficDataDownloader
  Library interface: ITrafficDataDownloader
  extra class: SourceManager implements ISourceManager
````

__[TrafficDataDownstreamAnalyser](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDownstreamAnalyzer)__

Deze analyser staat in voor generatie van meldingen bij verkeersproblemen en controleren op geldigheid van nieuwe data. De nieuwe data zal via deze klassen kunnen worden toegevoegd aan de databank.
````
Package name: iii.vop2016.verkeer2.ejb.downstream

EJB Bean: TrafficDataDownstreamAnalyser
  Session Beans: TrafficDataDownstreamAnalyser
  Library interface: ITrafficDataDownstreamAnalyser
````

### Providers

__[GoogleMapsSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GoogleMapsSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze API-calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: ISourceAdapter
````

__[HereSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/HereSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze API-calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: ISourceAdapter
````

### Data access Objects

__[GeneralDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GeneralDAO)__

De generalDAO is verantwoordelijk voor het beheer van routes die door de applicatie dienen in de gaten te worden gehouden. Deze worden gedefinieerd door een opeenvolging van geolocaties. De data worden verpakt door GeoLocationEntity en RouteEntity om deze compatibel te maken met de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: GeneralDAO
  Session Beans: GeneralDAO
  Library interface: IGeneralDAO
  extra class: GeoLocationEntity extends GeoLocation, RouteEntity extends Route
````

__[TrafficDataDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDAO)__

De trafficDataDAO is verantwoordelijk voor het beheer van data die betrekking hebben op de verkeerssituatie op een bepaald moment. De data worden verpakt door RouteDataEntity om deze compatibel te maken met de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: TrafficDataDAO
  Session Beans: TrafficDataDAO
  Library interface: ITrafficDataDAO
  extra class: RouteDataEntity extends RouteData
````

__[GeneralDAONoDB](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GeneralDAONoDB)__

De generalDAODummy is een dummy-DAO die de databank voor algemene gegegevens, zoals de informatie van Route en GeoLocation, simuleert. Deze kan worden gebruikt voor het uitvoeren van tests, zonder de echte databank te bevuilen.
````
Package name: iii.vop2016.verkeer2.ejb.dao.dummy

EJB Bean: GeneralDAONoDB
  Session Beans: GeneralDAONoDB
  Library interface: GeneralDAONoDBRemoete
````

__[TrafficDataDAONoDB](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDAONoDB)__

De trafficdataDAODummy is een dummy-DAO die de databank voor RouteData simuleert. Deze kan worden gebruikt voor het uitvoeren van tests, zonder de echte databank te bevuilen.
````
Package name: iii.vop2016.verkeer2.ejb.dao.dummy

EJB Bean: TrafficDataDAONoDB
  Session Beans: TrafficDataDAONoDB
  Library interface: TrafficDataDAONoDBRemote
````

### Presentation logic

__[RestApi](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/RestApi)__

Deze bean representeert de REST-service van het project. De REST-service onttrekt data uit het systeem en geeft deze vervolgens aan de gebruiker in JSON-formaat. Data ophalen gebeurt aan de hand van URL-patronen.
````
Package name: iii.vop2016.verkeer2.war.rest.

Web project: RestApi
Context path: /api/v2
Resources: /providers (ProviderResource), /routes (RoutesResource)
````

Voor meer informatie wordt er verwezen naar de [documentatie](http://docs.verkeerscentrumgent.apiary.io/)__

***

