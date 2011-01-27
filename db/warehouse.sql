CREATE TABLE administrationsvej
  (
     administrationsvejpid   BIGINT(15) NOT NULL AUTO_INCREMENT,
     administrationsvejkode  CHAR(2) NOT NULL,
     administrationsvejtekst VARCHAR(50) NOT NULL,
     modifiedby              VARCHAR(200) NOT NULL,
     modifieddate            DATETIME NOT NULL,
     validfrom               DATETIME,
     validto                 DATETIME,
     createdby               VARCHAR(200),
     createddate             DATETIME,
     INDEX (validfrom, validto),
     PRIMARY KEY (administrationsvejpid)
  )
ENGINE=INNODB;

CREATE TABLE apotek
  (
     apotekpid          BIGINT(15) NOT NULL AUTO_INCREMENT,
     sornummer          BIGINT(20) NOT NULL,
     apoteknummer       BIGINT(15),
     filialnummer       BIGINT(15),
     eanlokationsnummer BIGINT(20),
     cvr                BIGINT(15),
     pcvr               BIGINT(15),
     navn               VARCHAR(256),
     telefon            VARCHAR(20),
     vejnavn            VARCHAR(100),
     postnummer         VARCHAR(10),
     bynavn             VARCHAR(30),
     email              VARCHAR(100),
     www                VARCHAR(100),
     modifiedby         VARCHAR(200) NOT NULL,
     modifieddate       DATETIME NOT NULL,
     validfrom          DATETIME,
     validto            DATETIME,
     createdby          VARCHAR(200) NOT NULL,
     createddate        DATETIME NOT NULL,
     PRIMARY KEY (apotekpid),
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE atc
  (
     atcpid       BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     atc          VARCHAR(10) NOT NULL,
     atctekst     VARCHAR(72) NOT NULL,
     modifiedby   VARCHAR(200) NOT NULL,
     modifieddate DATETIME NOT NULL,
     validfrom    DATETIME,
     validto      DATETIME,
     createdby    VARCHAR(200) NOT NULL,
     createddate  DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE autorisation
  (
     autorisationpid     BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     autorisationsnummer VARCHAR(10) NOT NULL,
     cpr                 VARCHAR(10) NOT NULL,
     fornavn             VARCHAR(100) NOT NULL,
     efternavn           VARCHAR(100) NOT NULL,
     uddannelseskode     INT(4),
     modifiedby          VARCHAR(200) NOT NULL,
     modifieddate        DATETIME NOT NULL,
     validfrom           DATETIME,
     validto             DATETIME,
     createdby           VARCHAR(200) NOT NULL,
     createddate         DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE barnrelation
  (
     barnrelationpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     id              VARCHAR(21) NOT NULL,
     cpr             VARCHAR(10) NOT NULL,
     barncpr         VARCHAR(10) NOT NULL,
     modifiedby      VARCHAR(200) NOT NULL,
     modifieddate    DATETIME NOT NULL,
     validfrom       DATETIME,
     validto         DATETIME,
     createdby       VARCHAR(200) NOT NULL,
     createddate     DATETIME NOT NULL,
     INDEX (validfrom, validto),
     CONSTRAINT uc_person_1 UNIQUE (id, validfrom)
  )
ENGINE=INNODB;

CREATE TABLE dosering
  (
     doseringpid         BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     doseringkode        BIGINT(12) NOT NULL,
     doseringtekst       VARCHAR(100) NOT NULL,
     antalenhederprdoegn FLOAT(10) NOT NULL,
     aktiv               BOOLEAN,
     modifiedby          VARCHAR(200) NOT NULL,
     modifieddate        DATETIME NOT NULL,
     validfrom           DATETIME,
     validto             DATETIME,
     createdby           VARCHAR(200) NOT NULL,
     createddate         DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE foraeldremyndighedrelation
  (
     foraeldremyndighedrelationpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY
     KEY,
     id                            VARCHAR(21) NOT NULL,
     cpr                           CHAR(10) NOT NULL,
     typekode                      VARCHAR(4) NOT NULL,
     typetekst                     VARCHAR(50) NOT NULL,
     relationcpr                   CHAR(10),
     modifiedby                    VARCHAR(200) NOT NULL,
     modifieddate                  DATETIME NOT NULL,
     validfrom                     DATETIME,
     validto                       DATETIME,
     createdby                     VARCHAR(200) NOT NULL,
     createddate                   DATETIME NOT NULL,
     INDEX (validfrom, validto),
     CONSTRAINT uc_person_1 UNIQUE (id, validfrom)
  )
ENGINE=INNODB;

CREATE TABLE formbetegnelse
  (
     formbetegnelsepid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     kode              VARCHAR(10) NOT NULL,
     tekst             VARCHAR(150) NOT NULL,
     modifiedby        VARCHAR(200) NOT NULL,
     modifieddate      DATETIME NOT NULL,
     validfrom         DATETIME,
     validto           DATETIME,
     createdby         VARCHAR(200) NOT NULL,
     createddate       DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE indikation
  (
     indikationpid   BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     indikationkode  BIGINT(15),
     indikationtekst VARCHAR(100),
     modifiedby      VARCHAR(200) NOT NULL,
     modifieddate    DATETIME NOT NULL,
     validfrom       DATETIME,
     validto         DATETIME,
     createdby       VARCHAR(200) NOT NULL,
     createddate     DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE indikationatcref
  (
     indikationatcrefpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     cid                 VARCHAR(22) NOT NULL,
     indikationkode      BIGINT(15) NOT NULL,
     atc                 VARCHAR(10) NOT NULL,
     modifiedby          VARCHAR(200) NOT NULL,
     modifieddate        DATETIME NOT NULL,
     validfrom           DATETIME,
     validto             DATETIME,
     createdby           VARCHAR(200) NOT NULL,
     createddate         DATETIME NOT NULL,
     INDEX (validfrom, validto, indikationkode, atc)
  )
ENGINE=INNODB;

CREATE TABLE kommune
  (
     kommunepid   BIGINT(15) NOT NULL PRIMARY KEY,
     nummer       VARCHAR(12) NOT NULL,
     navn         VARCHAR(100) NOT NULL,
     modifiedby   VARCHAR(200) NOT NULL,
     modifieddate DATETIME NOT NULL,
     validfrom    DATETIME,
     validto      DATETIME,
     createdby    VARCHAR(200) NOT NULL,
     createddate  DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE laegemiddel
  (
     laegemiddelpid    BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     drugid            BIGINT(12) NOT NULL,
     drugname          VARCHAR(30) NOT NULL,
     formkode          VARCHAR(10),
     formtekst         VARCHAR(150),
     atckode           VARCHAR(10),
     atctekst          VARCHAR(100),
     styrkenumerisk    DECIMAL(10, 3),
     styrkeenhed       VARCHAR(100),
     styrketekst       VARCHAR(30),
     dosisdispenserbar BOOLEAN,
     modifiedby        VARCHAR(200) NOT NULL,
     modifieddate      DATETIME NOT NULL,
     validfrom         DATETIME,
     validto           DATETIME,
     createdby         VARCHAR(200) NOT NULL,
     createddate       DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE laegemiddeladministrationsvejref
  (
     laegemiddeladministrationsvejrefpid BIGINT(15) AUTO_INCREMENT NOT NULL
     PRIMARY
     KEY,
     cid                                 VARCHAR(22) NOT NULL,
     drugid                              BIGINT(12) NOT NULL,
     administrationsvejkode              CHAR(2) NOT NULL,
     modifiedby                          VARCHAR(200) NOT NULL,
     modifieddate                        DATETIME NOT NULL,
     validfrom                           DATETIME,
     validto                             DATETIME,
     createdby                           VARCHAR(200) NOT NULL,
     createddate                         DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE laegemiddeldoseringref
  (
     laegemiddeldoseringrefpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     cid                       VARCHAR(22) NOT NULL,
     drugid                    BIGINT(12) NOT NULL,
     doseringkode              BIGINT(12) NOT NULL,
     modifiedby                VARCHAR(200) NOT NULL,
     modifieddate              DATETIME NOT NULL,
     validfrom                 DATETIME,
     validto                   DATETIME,
     createdby                 VARCHAR(200) NOT NULL,
     createddate               DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE klausulering
  (
     klausuleringpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     kode            VARCHAR(10) NOT NULL,
     korttekst       VARCHAR(60),
     tekst           VARCHAR(600),
     modifiedby      VARCHAR(200) NOT NULL,
     modifieddate    DATETIME NOT NULL,
     validfrom       DATETIME,
     validto         DATETIME,
     createdby       VARCHAR(200) NOT NULL,
     createddate     DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE medicintilskud
  (
     medicintilskudpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     kode              VARCHAR(10) NOT NULL,
     korttekst         VARCHAR(20),
     tekst             VARCHAR(60),
     modifiedby        VARCHAR(200) NOT NULL,
     modifieddate      DATETIME NOT NULL,
     validfrom         DATETIME,
     validto           DATETIME,
     createdby         VARCHAR(200) NOT NULL,
     createddate       DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE organisation
  (
     organisationpid   BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     nummer            VARCHAR(30) NOT NULL,
     navn              VARCHAR(256),
     organisationstype VARCHAR(30) NOT NULL,
     modifiedby        VARCHAR(200) NOT NULL,
     modifieddate      DATETIME NOT NULL,
     validfrom         DATETIME,
     validto           DATETIME,
     createdby         VARCHAR(200) NOT NULL,
     createddate       DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE pakning
  (
     pakningpid                 BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     varenummer                 BIGINT(12) NOT NULL,
     varenummerdelpakning       BIGINT(12),
     drugid                     DECIMAL(12) NOT NULL,
     pakningsstoerrelsenumerisk DECIMAL(10, 2),
     pakningsstoerrelsesenhed   VARCHAR(10),
     pakningsstoerrelsetekst    VARCHAR(30),
     emballagetypekode          VARCHAR(10),
     dosisdispenserbar          BOOL,
     medicintilskudskode        VARCHAR(10),
     klausuleringskode          VARCHAR(10),
     modifiedby                 VARCHAR(200) NOT NULL,
     modifieddate               DATETIME NOT NULL,
     validfrom                  DATETIME,
     validto                    DATETIME,
     createdby                  VARCHAR(200) NOT NULL,
     createddate                DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE pakningsstoerrelsesenhed
  (
     pakningsstoerrelsesenhedpid   BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     pakningsstoerrelsesenhedkode  VARCHAR(10) NOT NULL,
     pakningsstoerrelsesenhedtekst VARCHAR(50) NOT NULL,
     modifiedby                    VARCHAR(200) NOT NULL,
     modifieddate                  DATETIME NOT NULL,
     validfrom                     DATETIME,
     validto                       DATETIME,
     createdby                     VARCHAR(200),
     createddate                   DATETIME,
     INDEX (validfrom, validto),
     CONSTRAINT uc_pakningsstoerrelsesenhed_1 UNIQUE (
     pakningsstoerrelsesenhedkode, validto)
  )
ENGINE=INNODB;

CREATE TABLE person
  (
     personpid                  BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     cpr                        VARCHAR(10) NOT NULL,
     koen                       VARCHAR(1) NOT NULL,
     fornavn                    VARCHAR(60),
     mellemnavn                 VARCHAR(60),
     efternavn                  VARCHAR(60),
     conavn                     VARCHAR(50),
     lokalitet                  VARCHAR(50),
     vejnavn                    VARCHAR(30),
     bygningsnummer             VARCHAR(10),
     husnummer                  VARCHAR(10),
     etage                      VARCHAR(10),
     sidedoernummer             VARCHAR(10),
     bynavn                     VARCHAR(30),
     postnummer                 BIGINT(12),
     postdistrikt               VARCHAR(30),
     status                     VARCHAR(2),
     navnebeskyttelsestartdato  DATETIME,
     navnebeskyttelseslettedato DATETIME,
     gaeldendecpr               VARCHAR(10),
     foedselsdato               DATETIME NOT NULL,
     stilling                   VARCHAR(50),
     vejkode                    BIGINT(12),
     kommunekode                BIGINT(12),
     modifiedby                 VARCHAR(200) NOT NULL,
     modifieddate               DATETIME NOT NULL,
     validfrom                  DATETIME,
     validto                    DATETIME,
     createdby                  VARCHAR(200) NOT NULL,
     createddate                DATETIME NOT NULL,
     INDEX (validfrom, validto),
     CONSTRAINT uc_person_1 UNIQUE (cpr, validfrom)
  )
ENGINE=INNODB;

CREATE TABLE personikraft
  (
     personikraftpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     ikraftdato      DATETIME NOT NULL
  )
ENGINE=INNODB;

CREATE TABLE praksis
  (
     praksispid         BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     sornummer          BIGINT(20) NOT NULL,
     eanlokationsnummer BIGINT(20),
     regioncode         BIGINT(12),
     navn               VARCHAR(256),
     modifiedby         VARCHAR(200) NOT NULL,
     modifieddate       DATETIME NOT NULL,
     validfrom          DATETIME,
     validto            DATETIME,
     createdby          VARCHAR(200) NOT NULL,
     createddate        DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE styrkeenhed
  (
     styrkeenhedpid   BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     styrkeenhedkode  VARCHAR(10) NOT NULL,
     styrkeenhedtekst VARCHAR(50) NOT NULL,
     modifiedby       VARCHAR(200) NOT NULL,
     modifieddate     DATETIME NOT NULL,
     validfrom        DATETIME,
     validto          DATETIME,
     createdby        VARCHAR(200),
     createddate      DATETIME,
     INDEX (validfrom, validto),
     CONSTRAINT uc_styrkeenhed_1 UNIQUE (styrkeenhedkode, validto)
  )
ENGINE=INNODB;

CREATE TABLE sygehus
  (
     sygehuspid         BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     sornummer          BIGINT(20) NOT NULL,
     eanlokationsnummer BIGINT(20),
     nummer             VARCHAR(30),
     telefon            VARCHAR(20),
     navn               VARCHAR(256),
     vejnavn            VARCHAR(100),
     postnummer         VARCHAR(10),
     bynavn             VARCHAR(30),
     email              VARCHAR(100),
     www                VARCHAR(100),
     modifiedby         VARCHAR(200) NOT NULL,
     modifieddate       DATETIME NOT NULL,
     validfrom          DATETIME,
     validto            DATETIME,
     createdby          VARCHAR(200) NOT NULL,
     createddate        DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE sygehusafdeling
  (
     sygehusafdelingpid        BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     sornummer                 BIGINT(20) NOT NULL,
     eanlokationsnummer        BIGINT(20),
     nummer                    VARCHAR(30),
     navn                      VARCHAR(256),
     sygehussornummer          BIGINT(20),
     overafdelingsornummer     BIGINT(20),
     underlagtsygehussornummer BIGINT(20),
     afdelingtypekode          BIGINT(20),
     afdelingtypetekst         VARCHAR(50),
     hovedspecialekode         VARCHAR(20),
     hovedspecialetekst        VARCHAR(40),
     telefon                   VARCHAR(20),
     vejnavn                   VARCHAR(100),
     postnummer                VARCHAR(10),
     bynavn                    VARCHAR(30),
     email                     VARCHAR(100),
     www                       VARCHAR(100),
     modifiedby                VARCHAR(200) NOT NULL,
     modifieddate              DATETIME NOT NULL,
     validfrom                 DATETIME,
     validto                   DATETIME,
     createdby                 VARCHAR(200) NOT NULL,
     createddate               DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE takstversion
  (
     takstversionpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     takstuge        VARCHAR(8) NOT NULL,
     modifiedby      VARCHAR(200) NOT NULL,
     modifieddate    DATETIME NOT NULL,
     validfrom       DATETIME,
     validto         DATETIME,
     createdby       VARCHAR(200) NOT NULL,
     createddate     DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE tidsenhed
  (
     tidsenhedpid   BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     tidsenhedkode  VARCHAR(10) NOT NULL,
     tidsenhedtekst VARCHAR(50) NOT NULL,
     modifiedby     VARCHAR(200) NOT NULL,
     modifieddate   DATETIME NOT NULL,
     validfrom      DATETIME,
     validto        DATETIME,
     createdby      VARCHAR(200),
     createddate    DATETIME,
     INDEX (validfrom, validto),
     CONSTRAINT uc_tidsenhed_1 UNIQUE (tidsenhedkode, validto)
  )
ENGINE=INNODB;

CREATE TABLE umyndiggoerelsevaergerelation
  (
     umyndiggoerelsevaergerelationpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARYKEY,
     id                               VARCHAR(21) NOT NULL,
     cpr                              VARCHAR(10) NOT NULL,
     typekode                         VARCHAR(4) NOT NULL,
     typetekst                        VARCHAR(50) NOT NULL,
     relationcpr                      VARCHAR(10),
     relationcprstartdato             DATETIME,
     vaergesnavn                      VARCHAR(50),
     vaergesnavnstartdato             DATETIME,
     relationstekst1                  VARCHAR(50),
     relationstekst2                  VARCHAR(50),
     relationstekst3                  VARCHAR(50),
     relationstekst4                  VARCHAR(50),
     relationstekst5                  VARCHAR(50),
     modifiedby                       VARCHAR(200) NOT NULL,
     modifieddate                     DATETIME NOT NULL,
     validfrom                        DATETIME,
     validto                          DATETIME,
     createdby                        VARCHAR(200) NOT NULL,
     createddate                      DATETIME NOT NULL,
     INDEX (validfrom, validto),
     CONSTRAINT uc_person_1 UNIQUE (id, validfrom)
  )
ENGINE=INNODB;

CREATE TABLE yder
  (
     yderpid            BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     nummer             VARCHAR(30),
     sornummer          BIGINT(20) NOT NULL,
     praksissornummer   BIGINT(20) NOT NULL,
     eanlokationsnummer BIGINT(20),
     telefon            VARCHAR(20),
     navn               VARCHAR(256),
     vejnavn            VARCHAR(100),
     postnummer         VARCHAR(10),
     bynavn             VARCHAR(30),
     email              VARCHAR(100),
     www                VARCHAR(100),
     hovedspecialekode  VARCHAR(20),
     hovedspecialetekst VARCHAR(40),
     modifiedby         VARCHAR(200) NOT NULL,
     modifieddate       DATETIME NOT NULL,
     validfrom          DATETIME,
     validto            DATETIME,
     createdby          VARCHAR(200) NOT NULL,
     createddate        DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE yderloebenummer
  (
     yderloebenummerpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     loebenummer        BIGINT(12) NOT NULL
  )
ENGINE=INNODB;

CREATE TABLE yderregister
  (
     yderregisterpid    BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     nummer             VARCHAR(30) NOT NULL,
     telefon            VARCHAR(10),
     navn               VARCHAR(256),
     vejnavn            VARCHAR(100),
     postnummer         VARCHAR(10),
     bynavn             VARCHAR(30),
     amtnummer          BIGINT(12),
     email              VARCHAR(100),
     www                VARCHAR(100),
     hovedspecialekode  VARCHAR(100),
     hovedspecialetekst VARCHAR(100),
     histid             VARCHAR(100),
     modifiedby         VARCHAR(200) NOT NULL,
     modifieddate       DATETIME NOT NULL,
     validfrom          DATETIME,
     validto            DATETIME,
     createdby          VARCHAR(200) NOT NULL,
     createddate        DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;

CREATE TABLE yderregisterperson
  (
     yderregisterpersonpid BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
     id                    VARCHAR(20) NOT NULL,
     nummer                VARCHAR(30) NOT NULL,
     cpr                   VARCHAR(10),
     personrollekode       BIGINT(20),
     personrolletxt        VARCHAR(200),
     histidperson          VARCHAR(100),
     modifiedby            VARCHAR(200) NOT NULL,
     modifieddate          DATETIME NOT NULL,
     validfrom             DATETIME,
     validto               DATETIME,
     createdby             VARCHAR(200) NOT NULL,
     createddate           DATETIME NOT NULL,
     INDEX (validfrom, validto)
  )
ENGINE=INNODB;
