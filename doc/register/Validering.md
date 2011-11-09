Taksten
=======

Dette register består af en række filer med filnavne:

system.txt
lms01.txt ... lms32.txt

== Sekvensering

1. Filen system.txt indeholder et takstuge hvor informationen er gældende.
   Denne takstuge (yyyyWW) bruges til at sætte de indlæste records gyldigheds
   periode. Da der kan kommer nye udgaver af registeret løbende i en takstuge,
   er dette ikke nok til at bestemme sekvensering.

2. Filen system.txt indeholder også et felt "Valid date of price list" som
   beskriver hvornår udtrækket er gældende. Dette bør nok anvendes i stedet.

== Integritet

3. Filen system.txt indeholden et felt 'Total number of files incl. system'
   denne kan bruges til at sikre sig at alle filer er med in datasættet.

4. Følgende filer er valgfrie i følge parser implementationen:

   lms32.txt, lms21.txt, lms22.txt, lms29.txt, lms30.txt, lms31.txt

   Det er dog svært at finde nogen information om hvorfor disse skulle være
   valgfrie.

5. Filen system.txt indeholder en snitflade-version.

== Eksisterende valideringer

5 er valideret for at checke at filerne har det forventede format.

== Forslag til forbedringer

1 - 2 er ikke valideret. Dog tror jeg at dette er tænkt som en feature, da det
er muligt at opdatere historisk data for at rette fejl. Fuld historik er ikke
at forvæksle med fuld audit-trail, hvilket stamdata ikke har.

Punkt 2 bør valideres. Datoen i punkt 2 er relateret til takstugen i punkt 1.
Det er derfor nødvendigt at holde versionsinformation for alle indlæste takst-
uger.


Yderregister
============

NB. Det virker umuligt at finde en beskrivelse af Yderregisters dataformat
så denne diskution bliver ud fra evidence fra testfiler.

Parseren forventer filerne:

SSR<104001300146>.K05.XML
SSR<104001300146>.K1025.XML
SSR<104001300146>.K40.XML
SSR<104001300146>.K45.XML
SSR<104001300146>.K5094.XML

NB. Tallene mellem klammerne er et eksempel.

== Sekvensering

1. Tallene mellem klammerne virker ikke, i testdataen, at være sekventielle.
   Det er uklart om dette er et løbenummer.

2. Hver fil indeholder en start record med et felt OpgDato. Denne kan muligvis
   bruges til at sikre sekvens.

== Integritet

3. Alle filer indeholder en start record med modtager id, og snitflade version.

4. Alle filer indeholder en slut record med antal records.

== Eksisterende valideringer

Punkt 3 og 4 tests allerede.

== Forslag til forbedringer

Punkt 2 bør valideres for at sikre sekvensen. Derud over bør det undersøges
hvorfor filerne K1025 K40 K45 K5094 er forventede men tilsyneladende altid
tomme. Det er de i hvert fald i al testdata. Det bør sikres at parseren ikke
bare læser alle XML filer som ligger i indboksen ind, men istedet sikre at
kun de forventede filer indlæses. 



Autorisationsregister
=====================

== Sekvensering

Filer fra Autorisationsregisteret er datomærket med format:

    <yyyyMMdd>AutReg.csv

1. Det kan derfor sikres at gamle udgaver af registeret overskriver nye.

2. Det kan ikke sikres at alle udgaver af registeret er modtaget da filnavnet
   eller filens indhold ikke indholder et løbenummer. Dette har dog ikke store
   konsikvenser. Da udtrækket altid indeholder et komplet registerudtræk. Dvs.
   at registeret ikke ender i en inkonsistent tilstand hvis ikke alle
   "mellemregninger" kommer med.

== Eksisterende valideringer

Punkt 1 valideres allerede.

== Forslag til forbedringer

3. Som det er nu importeres alle filer som ligge i indbakken i
   vilkårlig rækkefølge. Det er datomærket fra den første fil som bruges til
   at validere sekvensen med. Det bør valideres at der kun er én fil i
   indbakken for parseren.

== Ting der ikke er muligt at validere

Det er ikke muligt at validere at alle records er indeholdt i filerne. Der er
ikke et felt med antal records (linjer) i filen eller en start- og
slut-record.

Det Centrale Personregister
===========================

== Sekvensering

Filer fra CPR er datomærket med format:

    D<yyMMdd>.<opgavenummer>

1. I en CPR-fil er der en start-record  som indeholder
   tidsstempel for dette udtræk samt tidsstempel for forige udtræk. Ved at bruge
   tidsstempelet fra forige udtræk kan man sikre sig at der ikke er huller i
   sekvensen.

== Integritet

2. Opgavenummeret afhænger af kunden og hvilke recordtyper man har i sit
   udtræk.

3. CPR-filer indeholder en slut-record. Så det kan sikres at alle records er
   modtaget.

== Eksisterende valideringer

Punkt 1, 2 og 3 valideres allerede.

== Forslag til forbedringer

Som det er nu importeres alle filer som ligge i indbakken i
vilkårlig rækkefølge. Dog checkes rækkefølgen for alle filer. Det vil sige
at filer ikke kan blive indlæst i forkert rækkefølge. CPR parseren er dog
ikke bygget til at håndtere situationer med flere filer og vil vælge en
vilkårlig fil at at starte med. Er denne ikke den rigtige vil indlæsningen
fejle. Det bør valideres at der kun er én fil i indbakken for parseren. Så
er indlæsninger altid konsistente.


Doseringsforslag
================

== Sekvensering

Dette register modtages som et sæt af filer:

DosageVersion.json
DosageStructures.json
DosageUnits.json
Drugs.json
DrugsDosageStructures.json

1. Filen 'DosageVersion.json' indeholder et løbenummer som tælles én op for hvert
   udtræk.

== Integritet

2. Hver record i de andre filer indeholder et releaseNumber felt som refererer
   til løbenummeret fra 'DosageVersion.json'.

3. Der er ingen record-count felt i filerne, men grundet JSON formattet vil
   parseren fejle hvis ikke filen er helt indlæst.

== Eksisterende valideringer

   Punkt 1 valideres allerede og 3 sikres implicit. Punkt 2 er ikke valideret.

== Forslag til forbedringer

Hver record i de andre filer bør valideres for om de har samme løbenummer som
'DosageVersion.json'.


Sikrede
=======

== Sekvensering

NB. Dokumentationen for dette register er usikkeret da vi ikke har modtaget
testfiler.

1. Forfatteren af denne parser har antaget at filerne er navngivet med dato-
   mærket med format:
   
       <yyyyMMdd>.*

2. Hver fil indeholder en start-record med et løbenummer. Det er dog uklart
   om dette løbenummer kan anvendes til validering da det står angivet som
   et internt CSC løbenummer. Det ville dog være underligt hvis ikke det kunne
   anvendes.

== Integritet
   
3. Hver fil indeholder en slut-record hvor antallet af records står i.

== Eksisterende valideringer

Punkt 1 valideres, men bør nok ikke gøres.

== Forslag til forbedringer

Løbenummeret fra punkt 2 bør anvendes i stedet for punkt 1, under antagelse af
at det kan anvendes af eksterne.

Der bør tælles records ved indlæsning og punkt 3. valideres.

Under antagelse af at der kun er én fil i register udtræk bør dette også
valideres.


SKS
===

Dette register indlæses kun delvist. Navnligt er det kun SHAK (sygehudafdel-
inger) som indlæses. Disse oplysninger ligger i én fil:

SHAKCOMPLETE.TXT

== Sekvensering

Denne fil indeholder ingen datomærkning eller løbenummer.

== Eksisterende valideringer

Ingen.

== Forslag til forbedringer

Som parseren fungerer nu, læser den alle filer der slutter på TXT ind i
vilkårlig rækkefølge. Det bør valideres at der findes én fil med navnet
SHAKCOMPLETE.TXT.

Der ud over bør dataejer ændre filnavnet til at indeholde et løbenummer
og også gerne en dato.


SOR
===

Udtræk fra dette register indeholder én fil:

Sor.xml

== Sekvensering

1. Denne fil indeholder en datomærkning men intet eller løbenummer.

== Eksisterende valideringer

Ingen.

== Forslag til forbedringer

Som parseren fungerer nu, læser den alle filer der slutter på XML ind i
vilkårlig rækkefølge. Det bør valideres at der findes én fil med navnet
Sor.xml.

Der ud over bør dataejer ændre XML formattet til at indeholde et løbenummer
udover datoen. Man kan dog argumentere for at siden en ny fil oprettes hver
dag kan manglende "mellemregninger" læses udfra datomærkningen. Siden udtræk
altid er komplette gør manglende "mellemregninger" ikke så meget.





