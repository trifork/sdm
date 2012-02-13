Guide til byg, test og codecoverage af Stamdata
===============================================

Dette er et midlertidigt dokument der senere skal flettes ind i den word-baserede dokumentation for projektet.
Den bliver ikke skrevet direkte deri, da denne i øjeblikket er under kraftig omskrivning.

Byg uden tests
--------------

Hele projektet kan bygges med kommandoen

    mvn clean package -DskipTests

Integrationtests
----------------

Inden de fulde integrationstests kan køres skal der være opsat en fungerende database.
Dette gøres lettest ved at køre et target fra maven scripts.
Dette target antager at der er en fungerende MySQL database på localhost, samt at denne har en bruger "sdm" med kodeordet "papkasse".
Hvis dette er tilfældet vil følgende kommando oprette de fornødne databaser samt foretage unit såvel som integrationstest

    mvn -Dmaven.test.failure.ignore=false clean verify -PdropAndCreateDB -Ddb.username=<bruger> -Ddb.password=<kodeord>

hvor <bruger> og <kodeord> er for en bruger der har ret til at slette oprette 
databaser. Som standard er de sat til "root" uden kodeord.


Code coverage
-------------

Code coverage skal ligeledes måles med en database samt med integrationstests.
De nødvendige betingelser for MySQL databasen på localhost er som beskrevet ovenfor.

Udførsel af tests samt generering af HTML rapporter sker ved kommandoen

    mvn -Pparent,default clean install cobertura:cobertura site

De generede rapporters placering kan findes ved at udføre

    find . -name index.html | grep cobertura

og åbnes på Mac OS med kommandoen

   find . -name index.html | grep cobertura | xargs open

Performance test
----------------

Nedenstående er taget fra dokumentationen "Performance test" der endnu ikke er frigivet på Softwarebørsen.

Inden man kører test skal man konfigurere stamdata komponenterne til at køre i development mode. 
Dette gøres ved at i filerne:

     $JBOSS_HOME/server/default/conf/stamdata-authorization-lookup-ws.properties
     $JBOSS_HOME/server/default/conf/stamdata-batch-copy-ws.properties

Sæt følgende property:

    security=dgwsTest
og
    subjectSerialNumbers=CVR:19343634-UID:1234

Dette gør at komponenterne godkender requests med ID Kort underskrevet af Test STS’en.
Når filerne er ændret skal Jboss genstartes.

Det CVR-nummer der bliver brugt til tests skal være oprettet i stamdatatabellen "Client" og "Client_permissions". 
Dette gøres ved at oprette et element med "name" sat til "Region Syd" og "subjectSerialNumber" sat til "CVR:19343634-UID:1234" i tabellen ”Client”. 
Aflæs det generede id og brug dette som "client_id" i en ny indgang i tabellen "Client_permissions". 
Denne skal have værdien "cpr/person/v1" i feltet "permission".

Performance tests køres ved at køre følgende kommandoer fra common/performance i stamdata’s kildekode:

mvn -Pperformancetest integration-test site -Dhostname=<host> -Dport=<port>

Hvor host er adressen på cnsp serveren.
HTML rapporter bliver lagt i performance-tests/target/site/ når test er færdige.

Version af dokument
-------------------

0.1 2011-10-28 Første version af dokumentet /tgk@trifork.com
0.2 2011-12-29 Mindre ændringer af stavefejl /tgk@trifork.com