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

## Uitwerking project

Dit project werd uitgewerkt in java, met behulp van het java ee (Enterprise edition) framework. Dit framework omvat verschillende technologieen gebruikt voor dit project. Hieronder valt:
* _JavaServer Faces (JSF) 2.2_
* _Contexts and Dependency Injection for Java 1.1_
* _Enterprise JavaBeans (EJB) 3.2_
* _Java Persistence API (JPA) 2.1_
* _Java Transaction API (JTA) 1.2_
* _Java API for RESTful Web Services (JAX-RS) 2.0_

Aangevuld met glassfish voorzieningen
* _Java Naming and Directory Interface (JNDI)_
* _Java Database Connection (JDBC) connection pools_

__JSF__

Deze technologie laat toe webpaginas te genereren op basis van data in de applicatie. Deze data wordt aangeleverd via 'backing beans'. Over de jaren is javaserver faces uitgebreid met ajax ondersteuning alsook listeners bij data wijzigingen. Dit alles laat toe een zeer flexibele, responsive interface te maken voor de gebruikers.

__Contexts and Dependency Injection__

Alle beans verwachten zekere diensten waarvan zijzelf afhangen, zo worden bij de Data access beans (DAO) gebruik gemaakt van de entitymanager voor interactie met da databank, bij de Downloader wordt dan weer de context van de application server verwacht om andere beans op te vragen. Al deze diensten zijn niet de verantwoordelijkheid van deze indivituele beans maar van de application server. Deze laatste gedraagt zich als injector en zal alle vereiste services (dependencies) injecten in de beans aan de hand van annotaties en objecttypes.

__EJB__

Javabeans zijn door software beheerde modulaire bouwblokken. In deze beans wordt de business logic voor een enterprise applicatie verwerkt. De grootste kenmerken voor deze beans zijn hun modulariteit, onafhankelijkheid van elkaar en schaalbaarheid. 

_Modulariteit_

Iedere module (ejb) in het project is uitwisselbaar met een nieuwe module. Dit is instelbaar in een extern properties bestand. Zo kan op ieder moment een provider worden toegevoegd, een database worden vervangen door een andere, een nieuwe web service worden toegevoegd, etc.

_Onafhankelijkheid_

Alle beans staan op zich, naast de gemeenschappelijke library waar beanoverschrijdende objecten (zoals interfaces) in staan zal geen enkele bean een invloed ondervinden bij het uitwisselen van andere modules. Toegang na vervangen van een module blijft via naamopvraging naar JNDI, welke de nieuwe module in plaats van de oude zal teruggeven.

_Uitbreidbaarheid_

De modules staan niet enkel op zich, maar hebben eveneens geen band met de locatie waar ze werken. Zo kan een database bean op een andere server draaien dan de analyser bean. De enige vereiste hiervoor is dat JNDI van de ene server gelinked is aan de JNDI van de andere server. De beans zullen hun paramaters en return values steeds serialiseren en doorsturen naar de 'remote' bean.

__JPA__

De java variant voor object relational mapping (ORM) laat toe de gegevens in een databank rechtstreeks af te beelden op objecten door middel van annotaties. Deze mannier van databank interactie laat een zeer eenvoudige werking toe voor data opslag en afhaling. Wel moet opgemerkt worden dat dit niet aan de performatie van handmatige sql commando's zal bereiken.

__JTA__

JTA is de Transaction api in java ee. Deze api start (zonder enige configuratie) steeds een transactie bij het aanroepen van een functie in een managed bean. Indien die functie er error zou opwerpen zal een rollback gebeuren tot de toestand vlak voor de aanroep van de desbetreffende functie. In het project wordt op deze api vertrouwt voor opslag van gegevens in de databank, alsook algemene opvanging van fouten. Bij een error zal het datavergaren voor dat interval niet plaatsvinden, maar zal de applicatie wel blijven werken.

__JAX-RS__

De api voor RESTfull webservices laat toe om services aan te bieden volgens het Representational State Transfer (REST) architectural pattern. In dit project is dit aangewend om de api uit te werken. Deze api laat toe data op te vragen via url paths, zo geven de paths de gevraagde data in json formaat terug.

__JNDI__

Deze technologie laat toe data of objecten op te vragen via naam. Voor dit project werd de link naar de resource files, de link naar de JDBC connection pool en de link naar alle beans (automatisch door het framework) opgenomen in JNDI. 

__JDBC connection pools__

Een connection pool houdt een cache van connecties naar een welbepaalde databank bij en maakt deze beschikbaar aan de applicaties van de application server. Naast een hogere efficientie door het behouden en hergebruik van connecties wordt er ook een hogere veiligheid verondersteld aangezien de connectieparameters niet langer in de applicatie aanwezig zijn.

***

## Structuur project

***
