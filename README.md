# verkeer-2

## Inhoudstabel

* Inloggegevens werkende omgeving uitgerust met dit project:
* Installeren project
* Structuur repository
* Structuur project

## Inloggegevens werkende omgeving uitgerust met dit project:

_Waarschuwing: Dit project is volledig uitgetest met de Chrome browser en Edge browser. Gelieve een van deze browsers te gebruiken, andere browsers geven mogenlijks een onvoorspelbare werking._

Url webapplicatie: verkeer-2.bp.tiwi.be
Login authenticatie UGENT: username: root, passwoord: Wc7miuZpA6 (geldig gedurende 1 browsersessie)
Login authenticatie applicatie: username: root, passwoord: root (geldig gedurende 30min)

SSH: root@146.185.151.29 met passwoord: Wc7miuZpA6

Glassfish: username: admin, passwoord: Wc7miuZpA6

MySql: username: root, passwoord: root

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

Dit project is een modulair systeem bestaande uit 10tallen verschillende java enterprise beans. Om deze allen aan te bieden in 1 geheel , alsook het management hiervan is in de [distribution](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/distribution) map een verzameling aangelegd. In het vervolg van deze tekst zal hiernaar vermeld worden als '_installatiemap_' of '_setupmap_'. 

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
* 

#### Inladen modules

> Gebruik als laatste het '_./cycle.sh_' script om alle beans in te laden in glassfish en het systeem op te starten voor gebruik. Let hierbij niet op de output van de eerste commando's, Dit script dient eigenlijk om een draaiende server volledig opnieuw op te starten.

Via de admin console van glassfish kunnen alle modules worden ingeladen. Dit kan onder de tab 'Applications'

Login op de admin console interface. Consulteer uw administrator om het poortnummer en logingegevens te verkrijgen. Voor de default poort bevindt zich de interface op http://server_adres:4848

Onder de tab 'Applications' kan je module per module toevoegen dia het 'deploy' commando. Zorg ervoor dat steeds alle eigenschappen voor de bean zijn ingevuld. Het is aangeraden het linux bash script '_./cycle.sh_' te bekijken om alle eigenschappen te zien van iedere bean.


### Beheer systeem

#### Start - stop

Er zijn 2 scripts aanwezig om de services te starten / stoppen: '_./start.sh_' start de glassfish, '_./stop.sh_' stopt de service.

#### Logs bekijken

'_./viewlog.sh_' heeft een link welke de glassfish logs opent via een append mechanisme. Je krijgt live de oude en nieuwere logs te zien tot het script wordt onderbroken.

#### Aanpassen systeem

Via '_./deploy.sh_' kan je modules die niet geladen zijn inladen. Indien je een nieuwe module zelf geschreven hebt, kan je deze toevoegen aan het script door de vorige modules in het script te analyseren. Zolang de core 'VerkeersLib' van de applicatie niet is veranderd kan dit zonder de server te herstarten.

Via '_./redeploy.sh_' kan je een geladen module opnieuw inladen. Dit kan gebruikt worden indien een ontwikkelaar een module heeft gewijzigd en wilt toepassen op de server terwijl deze nog draaiende is.

via '_./undeploy.sh_' kan je een module terug uitladen. Het wordt ten sterkste afgeraden om een uitgeladen module terug in te laden via deploy. Als resources verbonden waren aan deze bean bestaat de kans dat de nieuwe deze resources niet kan alloceren. 

Alle modules bevinden zich in de submap _jars/verkeer2/_ van de installatiemap. Deze kunnen hier worden gewijzigd en vervolgens opnieuw ingeladen via bovenstaande commando's

Via '_./cycle.sh_' kan je alle modules uitladen en opnieuw laten inladen. Deze methode stopt de server en start deze opnieuw op. De functie wordt vooral gebruikt bij aanpassingen als de core 'VerkeerLib' van de applicatie is gewijzigd en moet worden doorgevoegd naar alle modules.

Als men via netbeans of dergelijke aan debugging wenst te doen kan men de server ook opstarten via '_./debug.sh_'

***


## Structuur repository
__[Root/](https://github.ugent.be/iii-vop2016/verkeer-2)__

Deze map bevat de logboeken, dit README-bestand en GNU license.

__[Analyse/](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Analyse)__

De map bevat alle analysedocumenten van het project.

__[distribution/](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/distribution)__

Deze map bevat een setup- / intallatiemap van het project.

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
  Library interface: LoggerRemote, LoggerLocal
````

__[TimerScheduler](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TimerScheduler)__

Deze bean start mee op met de server en creëert een timer via Java EE TimerServices die volgens een patroon, gedefinieerd in properties file '[TimerScheduler](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/TimerScheduler.properties)', de download van data triggert.
````
Package name: iii.vop2016.verkeer2.ejb.timer

EJB Bean: TimerScheduler
  Session Beans: TimerScheduler
  Library interface: TimerRemote, TimerLocal
````

__[TrafficDataDownloader](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDownloader)__

De downloader staat in voor de connectie tussen de providers en de databases. Deze klasse, met slechts één enkele methode, wordt getriggerd vanuit de TimerServices, vraagt data over de routes en vraagt de verkeerssituatie op aan de providers. Deze data wordt vervolgens naar de trafficDataDAO voor dataopslag gepushed via de DownstreamAnalyser, die de desbetreffende data zal controleren en eventuele meldingen zal genereren. De providers worden beheerd door de extra klasse SourceManager.
````
Package name: iii.vop2016.verkeer2.ejb.datadownloader

EJB Bean: TrafficDataDownloader
  Session Beans: TrafficDataDownloader
  Library interface: TrafficDataDownloaderRemote, TrafficDataDownloaderLocal
  extra class: SourceManager implements ISourceManager
````

__[TrafficDataDownstreamAnalyser](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDownstreamAnalyzer)__

Deze analyser staat in voor generatie van meldingen bij verkeersproblemen en controleren op geldigheid van nieuwe data. De nieuwe data zal via deze klassen kunnen worden toegevoegd aan de databank.
````
Package name: iii.vop2016.verkeer2.ejb.downstream

EJB Bean: TrafficDataDownstreamAnalyser
  Session Beans: TrafficDataDownstreamAnalyser
  Library interface: TrafficDataDownstreamAnalyserRemote, TrafficDataDownstreamAnalyserLocal
````

__[DataProvider](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/DataProvider)__

Deze provider is de link tussen de presentatie layer en de data. Hij vormt de ruwe gegevens opgeslagen in de databank, verkregen van de dao's, om naar analyses en verzamelingen van data. Deze worden van via de presentation layer aangeboden aan de eindgebruiker.
````
Package name: iii.vop2016.verkeer2.ejb.dataprovider

EJB Bean: DataProvider
  Session Beans: DataProvider
  Library interface: DataProviderRemote, DataProviderLocal
````

__[GeoJsonProvider](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GeoJsonProvider)__

De geojsonprovider laat toe van de routes aanwezig in de applicatie een gestandariseerde json aan te bieden dat de route beschrijft op een wereldkaart. Op deze mannier kan een mapgenerator eenvoudig de verkregen route uittekenen.
````
Package name: iii.vop2016.verkeer2.ejb.geojson

EJB Bean: GeoJsonProvider
  Session Beans: GeoJsonProvider
  Library interface: GeoJsonProviderRemote, GeoJsonProviderLocal
````

__[Properties](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/Properties)__

properties is een zeer eenvoudig registratiesysteem voor globale variabelen gebruikt in meerdere beans. Dit dient hoofdzakelijk als verzamelpunt van alle propertyfiles.
````
Package name: iii.vop2016.verkeer2.ejb.properties

EJB Bean: Properties
  Session Beans: Properties
  Library interface: PropertiesLocal, PropertiesRemote
````

__[Thresholds](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/Thresholds)__

De ThresholdManager houdt van alle routes triggerpunten bij. Als blijkt dat een bepaald triggerpunt overschreden wordt bij afhalen van nieuwe data zal deze aan zijn handlers vragen de nodige acties uit te voeren.
````
Package name: iii.vop2016.verkeer2.ejb.thresholds

EJB Bean: Thresholds
  Session Beans: ThresholdManager
  Library interface: ThresholdManagerRemote, ThresholdManagerLocal
````

__[VerkeerLibToJson](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/VerkeerLibToJson)__

Een Class interface om eenvoudig objecten te delen met andere componenten via json. Alles basiscomponenten kunnen via deze interface worden omgezet naar json en omgekeerd.
````
Package name: iii.vop2016.verkeer2.ejb.helper
````

### Handlers

__[TwitterHandler](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TwitterHandler)__

Een van de handlers aanspreekbaar door de thresholdManager. Deze kan, indien verbonden aan een threshold, waarschuwingen genereren op twitter voor een bepaalde route met een bepaalde vertraging.
````
Package name: iii.vop2016.verkeer2.ejb.twitter

EJB Bean: TwitterHandler
  Session Beans: TwitterHandler
  Library interface: TwitterHandlerRemote, TwitterHandlerLocal
````
### Providers

__[GoogleMapsSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GoogleMapsSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze API-calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: SourceAdapterRemote, SourceAdapterLocal
````

__[HereSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/HereSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze API-calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: SourceAdapterRemote, SourceAdapterLocal
````

__[WazeSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/WazeSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze API-calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: WazeSourceAdapter
  Session Beans: WazeSourceAdapter
  Library interface: SourceAdapterRemote, SourceAdapterLocal
````

__[CoyoteSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/CoyoteSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze API-calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: CoyoteSourceAdapter
  Session Beans: CoyoteSourceAdapter
  Library interface: SourceAdapterRemote, SourceAdapterLocal
````

__[TomTomSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TomTomSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze API-calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: TomTomSourceAdapter
  Session Beans: TomTomSourceAdapter
  Library interface: SourceAdapterRemote, SourceAdapterLocal
````

### Data access Objects

__[GeneralDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GeneralDAO)__

De generalDAO is verantwoordelijk voor het beheer van routes die door de applicatie dienen in de gaten te worden gehouden. Deze worden gedefinieerd door een opeenvolging van geolocaties. De data worden verpakt door GeoLocationEntity en RouteEntity om deze compatibel te maken met de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: GeneralDAO
  Session Beans: GeneralDAO
  Library interface: GeneralDAORemote, GeneralDAOLocal
  extra class: GeoLocationEntity extends GeoLocation, RouteEntity extends Route
````

__[TrafficDataDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDAO)__

De trafficDataDAO is verantwoordelijk voor het beheer van data die betrekking hebben op de verkeerssituatie op een bepaald moment. De data worden verpakt door RouteDataEntity om deze compatibel te maken met de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: TrafficDataDAO
  Session Beans: TrafficDataDAO
  Library interface: TrafficDataDAORemote, TrafficDataDAOLocal
````

__[APIKeyDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/APIKeyDAO)__

Deze dao staat in voor het beheer van de apikeys gebruikt voor authenticatie van afgeschermde delen van de REST service
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: APIKeyDAO
  Session Beans: APIKeyDAO
  Library interface: APIKeyDAORemote, APIKeyDAOLocal
````

__[LoginDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/LoginDAO)__

Deze dao staat in voor het beheer van de gebruikers van de applicatie.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: LoginDAO
  Session Beans: LoginDAO
  Library interface: LoginDAORemote, LoginDAOLocal
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

