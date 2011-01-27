CREATE TABLE import 
  ( 
     importtime  DATETIME, 
     spoolername VARCHAR(100) 
  ) 
ENGINE=INNODB;

CREATE TABLE adressebeskyttelse 
  ( 
     cpr                        VARCHAR(10) NOT NULL, 
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
     navnebeskyttelsestartdato  DATETIME, 
     navnebeskyttelseslettedato DATETIME, 
     vejkode                    BIGINT(12), 
     kommunekode                BIGINT(12), 
     UNIQUE INDEX (cpr) 
  ) 
ENGINE=INNODB; 