# verkeer-2

***

## Structuur repository
__[Root](https://github.ugent.be/iii-vop2016/verkeer-2)__

Bevat de logboeken, dit readme bestand en GNU license.

__[Analyse](https://github.ugent.be/iii-vop2016/verkeer-2/Analyse)__

Bevat alle analysedocumenten van het project.

__[Realisatie](https://github.ugent.be/iii-vop2016/verkeer-2/Realisatie)__

Bevat alle source code van het project, alsook de property files gebruikt tijdens deployment.

Iedere directory is een aparte EJB (Enterprise java bean) samen met VerkeersLib dat de gemeenschappelijke library van het project voorstelt.

De lib directory bevat de gebruikte external libraries.

***

## Structuur project

### Business logic

__[Logger](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/Logger)__

Deze bean start mee op met de server en creeert een datasink voor logging naar een bestand. (log.txt)
````
Package name: iii.vop2016.verkeer2.ejb.logger

EJB Bean: Logger
  Session Beans: Logger
  Library interface: LoggerRemote
````

__[TimerScheduler](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TimerScheduler)__

Deze bean start mee op met de server en creeert een timer via java ee TimerServices die volgens het patroon gedefineerd in properties file 'TimerScheduler' triggert.
````
Package name: iii.vop2016.verkeer2.ejb.timer

EJB Bean: TimerScheduler
  Session Beans: TimerScheduler
  Library interface: ITimer
````

__[TrafficDataDownloader](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDownloader)__

De downloader staat in voor de connectie tussen de providers en de databases. Hij wordt getriggerd vanuit de TimerServices, vraagt data van de routes,verkregen uit de general dao, aan de providers. Deze data pusht hij vervolgens naar de trafficdata dao voor dataopslag en naar de analyzer voor generatie meldingen. De providers worden beheerd doo de extra klasse SourceManager.
````
Package name: iii.vop2016.verkeer2.ejb.datadownloader

EJB Bean: TrafficDataDownloader
  Session Beans: TrafficDataDownloader
  Library interface: ITrafficDataDownloader
  extra class: SourceManager implements ISourceManager
````

__[TrafficDataDownstreamAnalyzer](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDownstreamAnalyzer)__

Deze analyzer staat in voor generatie van meldingen. Hij wordt iedere keer als er data wordt gescrubt voorzien van deze nieuwe data om vergelijkingen uit te voeren.
````
Package name: iii.vop2016.verkeer2.ejb.downstream

EJB Bean: TrafficDataDownstreamAnalyser
  Session Beans: TrafficDataDownstreamAnalyser
  Library interface: IAnalyzer
````

### Providers

__[GoogleMapsSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GoogleMapsSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze api calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: ISourceAdapter
````

__[HereSourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/HereSourceAdapter)__

Providers leveren nieuwe trajectdata aan de applicatie. Hiervoor maken ze api calls naar hun target of scrubben ze de website.
````
Package name: iii.vop2016.verkeer2.ejb.datasources

EJB Bean: GoogleMapsSourceAdapter
  Session Beans: GoogleMapsSourceAdapter
  Library interface: ISourceAdapter
````

### Data access Objects

__[GeneralDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GeneralDAO)__

De general dao houdt de routes bij die door de applicatie dienen in de gaten te worden gehouden. Dit wordt bijgehouden aan de hand van geolocaties gelinkt als tussenpunten in routes. De verkregen data uit het programma wordt omhult in GeoLocationEntity en RouteEntity om deze compatibel te maken voor de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: GeneralDAO
  Session Beans: GeneralDAO
  Library interface: IGeneralDAO
  extra class: GeoLocationEntity extends GeoLocation, RouteEntity extends Route
````

__[TrafficDataDAO](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDAO)__

De trafficdata dao houdt alle data bij van de routes verkregen door de applicatie. De verkregen data uit het programma wordt omhult in RouteDataEntity om deze compatibel te maken voor de achterliggende databank.
````
Package name: iii.vop2016.verkeer2.ejb.dao

EJB Bean: TrafficDataDAO
  Session Beans: TrafficDataDAO
  Library interface: ITrafficDataDAO
  extra class: RouteDataEntity extends RouteData
````

__[GeneralDAONoDB](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/GeneralDAONoDB)__

De general dao dummy is een dummy database voor opslag van routes. Deze kan worden gebruikt voor het uitvoeren van tests, zonder de echte databank te bevuilen.
````
Package name: iii.vop2016.verkeer2.ejb.dao.dummy

EJB Bean: GeneralDAONoDB
  Session Beans: GeneralDAONoDB
  Library interface: GeneralDAONoDBRemoete
````

__[TrafficDataDAONoDB](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/TrafficDataDAONoDB)__

De trafficdata dao summy is een dummy database voor opslag van routedata. Deze kan worden gebruikt voor het uitvoeren van tests, zonder de echte databank te bevuilen.
````
Package name: iii.vop2016.verkeer2.ejb.dao.dummy

EJB Bean: TrafficDataDAONoDB
  Session Beans: TrafficDataDAONoDB
  Library interface: TrafficDataDAONoDBRemote
````

### Presentation logic

__[RestApi](https://github.ugent.be/iii-vop2016/verkeer-2/tree/master/Realisatie/RestApi)__

Deze bean omhult de Rest service van het project. De restservice trekt data uit een analyzer of dao en geeft deze terug in json formaat. Data ophalen gebeurt aan de hand van url paths.
````
Package name: iii.vop2016.verkeer2.war.rest.

Web project: RestApi
Context path: /api/v2
Resources: /providers (ProviderResource), /routes (RoutesResource)
````

***
