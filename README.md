# verkeer-2

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

## Uitvoeren project

Vooraleer dit project kan worden uitgevoerd zullen enkele aanpassingen nodig zijn aan Glassfish.

Dit project werd getest met het systeem [hier beschreven](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Hardware).

Volgend stappenplan moet doorlopen worden vooraleer het project kan worden gedeployed.
* Voer allereerst alle [fixes](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Glassfish---Fixes) door in Glassfish
* Voeg de [externe libraries](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Externe-libraries) toe aan Glassfish
* Herstart Glassfish
* Voeg [JNDI resources](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Glassfish-Resources) toe
* Voeg [JDBC connectie](https://github.ugent.be/iii-vop2016/verkeer-2/wiki/Glassfish---JDBC) toe
* Start MySQL-server en zorg ervoor dat er een database 'verkeer2' bestaat die leeg is
 
Afhankelijk van een lokale server of remote server dient een iets ander stappenplan te worden gevolgd.

Lokale server
* Verifieer dat de [Beans](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/Beans.properties) en [SourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/SourceAdaptors.properties) property-bestanden de applicatienaam voor hun JNDI naam hebben staan. Deze moeten voldoen aan volgend formaat:  java:global/ApplicatieNaam/ContainerNaam/BeanNaam. voorbeeld= java:global/Verkeer2/TrafficDataDAO/TrafficDataDAO
* 'Clean and build' allereerst VerkeersLib, hierna iedere bean en vervolgens de applicatie zelf.
* Vanuit netbeans kan het project onmiddellijk deployed worden via de java ee applicatie 'Verkeer2'

Externe server (hier wordt iedere bean apart gedeployed)
* Verifieer dat de [Beans](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/Beans.properties) en [SourceAdapter](https://github.ugent.be/iii-vop2016/verkeer-2/blob/master/Realisatie/SourceAdaptors.properties) property-bestanden de applicatienaam niet voor hun JNDI naam hebben staan. Deze moeten voldoen aan volgend formaat:  java:global/ContainerNaam/BeanNaam. voorbeeld= java:global/TrafficDataDAO/TrafficDataDAO
* 'Clean and build' allereerst VerkeersLib, hierna iedere bean en vervolgens de applicatie zelf.
* In de adminconsole van Glassfish: voeg iedere module toe onder de 'Applications' tab. Deploy steeds met alle libraries op te geven in het veld 'libraries'. Voor de REST-applicatie geef als contextroot 'api' op.
* Herstart de server
* Na het herstarten zal de applicatie worden opgestart en zijn taken vervullen.


***
