# verkeer-2

***

## Structuur repository
__Root__

Bevat de logboeken, dit readme bestand en GNU license.

__Analyse__

Bevat alle analysedocumenten van het project.

__Realisatie__

Bevat alle source code van het project, alsook de property files gebruikt tijdens deployment.

Iedere directory is een aparte EJB (Enterprise java bean) samen met VerkeersLib dat de gemeenschappelijke library van het project voorstelt.

De lib directory bevat de gebruikte external libraries.

***

## Structuur project

### Business logic

__Logger__

Deze bean start mee op met de server en creeert een datasink voor logging naar een bestand. (log.txt)
````
Package name: iii.vop2016.verkeer2.ejb.logger

EJB Bean: Logger
  Session Beans: Logger
  Library interface: LoggerRemote
````

__TimerScheduler__

Deze bean start mee op met de server en creeert een timer via java ee TimerServices die volgens het patroon gedefineerd in properties file 'TimerScheduler' triggert.
````
Package name: iii.vop2016.verkeer2.ejb.timer

EJB Bean: TimerScheduler
  Session Beans: TimerScheduler
  Library interface: ITimer
````

__TrafficDataDownloader__

De downloader staat in voor de connectie tussen de providers en de databases. Hij wordt getriggerd vanuit de TimerServices, vraagt data van de routes,verkregen uit de general dao, aan de providers. Deze data pusht hij vervolgens naar de trafficdata dao voor dataopslag en naar de analyzer voor generatie meldingen. De providers worden beheerd doo de extra klasse SourceManager.
````
Package name: iii.vop2016.verkeer2.ejb.datadownloader

EJB Bean: TrafficDataDownloader
  Session Beans: TrafficDataDownloader
  Library interface: ITrafficDataDownloader
  extra class: SourceManager implements ISourceManager
````

__TrafficDataDownstreamAnalyzer__

Deze analyzer staat in voor generatie van meldingen. Hij wordt iedere keer als er data wordt gescrubt voorzien van deze nieuwe data om vergelijkingen uit te voeren.
````
Package name: iii.vop2016.verkeer2.ejb.downstream

EJB Bean: TrafficDataDownstreamAnalyser
  Session Beans: TrafficDataDownstreamAnalyser
  Library interface: IAnalyzer
````

### Providers

__GoogleMapsSourceAdapter__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze api calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: ISourceAdapter
````

__HereSourceAdapter__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze api calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: ISourceAdapter
````

### Data access Objects

__GeneralDAO__

De general dao houdt de routes bij die door de applicatie dienen in de gaten te worden gehouden. Dit wordt bijgehouden aan de hand van geolocaties gelinkt als tussenpunten in routes. De verkregen data uit het programma wordt omhult in GeoLocationEntity en RouteEntity om deze compatibel te maken voor de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: GeneralDAO
  Session Beans: GeneralDAO
  Library interface: IGeneralDAO
  extra class: GeoLocationEntity extends GeoLocation, RouteEntity extends Route
````

__TrafficDataDAO__

De trafficdata dao houdt alle data bij van de routes verkregen door de applicatie. De verkregen data uit het programma wordt omhult in RouteDataEntity om deze compatibel te maken voor de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: TrafficDataDAO
  Session Beans: TrafficDataDAO
  Library interface: ITrafficDataDAO
  extra class: RouteDataEntity extends RouteData
````

__GeneralDAONoDB__

De general dao dummy is een dummy database voor opslag van routes. Deze kan worden gebruikt voor het uitvoeren van tests, zonder de echte databank te bevuilen.
````
Package name: iii.vop2016.verkeer2.ejb.dao.dummy

EJB Bean: GeneralDAONoDB
  Session Beans: GeneralDAONoDB
  Library interface: GeneralDAONoDBRemoete
````

__TrafficDataDAONoDB__

De trafficdata dao summy is een dummy database voor opslag van routedata. Deze kan worden gebruikt voor het uitvoeren van tests, zonder de echte databank te bevuilen.
````
Package name: iii.vop2016.verkeer2.ejb.dao.dummy

EJB Bean: TrafficDataDAONoDB
  Session Beans: TrafficDataDAONoDB
  Library interface: TrafficDataDAONoDBRemote
````

### Presenation logic

__RestApi__

Deze bean omhult de Rest service van het project. De restservice trekt data uit een analyzer of dao en geeft deze terug in json formaat. Data ophalen gebeurt aan de hand van url paths.
````
Package name: iii.vop2016.verkeer2.war.rest.

Web project: RestApi
Context path: /api/v2
Resources: /providers (ProviderResource), /routes (RoutesResource)
````

***
