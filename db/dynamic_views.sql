-- ---------------------------------------------------------------------------------------------------------------------
-- Denne fil indeholder dynamiske view beskrivelser
-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------
-- Doserings forslag

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('doseringsforslag', 'dosagestructure', 1, 'DosageStructure', NOW());
SET @lastDosStr := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='doseringsforslag' AND datatype='dosagestructure' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDosStr, 1, 'DosageStructurePID',               NULL, 0, -5, NULL),
(@lastDosStr, 0, 'releaseNumber',         'releaseNumber', 2, -5, NULL),
(@lastDosStr, 0, 'code',                           'code', 1, 12, NULL),
(@lastDosStr, 0, 'type',                           'type', 3, 12, NULL),
(@lastDosStr, 0, 'simpleString',           'simpleString', 4, 12, NULL),
(@lastDosStr, 0, 'supplementaryText', 'supplementaryText', 5, 12, NULL),
(@lastDosStr, 0, 'xml',                             'xml', 6, 12, NULL),
(@lastDosStr, 0, 'shortTranslation',   'shortTranslation', 7, 12, NULL),
(@lastDosStr, 0, 'longTranslation',     'longTranslation', 8, 12, NULL),
(@lastDosStr, 0, 'ModifiedDate',                     NULL, 0, 93, 12),
(@lastDosStr, 0, 'ValidFrom',                 'validFrom', 9, 93, 12),
(@lastDosStr, 0, 'ValidTo',                     'validTo',10, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('doseringsforslag', 'dosageunit', 1, 'DosageUnit', NOW());
SET @lastDosUni := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='doseringsforslag' AND datatype='dosageunit' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDosUni, 1, 'DosageUnitPID',            NULL, 0, -5, NULL),
(@lastDosUni, 0, 'code',                   'code', 2, -5, NULL),
(@lastDosUni, 0, 'releaseNumber', 'releaseNumber', 1, -5, NULL),
(@lastDosUni, 0, 'textSingular',   'textSingular', 3, 12, NULL),
(@lastDosUni, 0, 'textPlural',       'textPlural', 4, 12, NULL),
(@lastDosUni, 0, 'ModifiedDate',             NULL, 0, 93, 12),
(@lastDosUni, 0, 'ValidFrom',         'validFrom', 5, 93, 12),
(@lastDosUni, 0, 'ValidTo',             'validTo', 6, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('doseringsforslag', 'version', 1, 'DosageVersion', NOW());
SET @lastDosVer := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='doseringsforslag' AND datatype='version' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDosVer, 1, 'DosageVersionPID',         NULL, 0, -5, NULL),
(@lastDosVer, 0, 'daDate',               'daDate', 1, 91, NULL),
(@lastDosVer, 0, 'lmsDate',             'lmsDate', 2, 91, NULL),
(@lastDosVer, 0, 'releaseDate',     'releaseDate', 3, 91, NULL),
(@lastDosVer, 0, 'releaseNumber', 'releaseNumber', 4, -5, NULL),
(@lastDosVer, 0, 'ModifiedDate',             NULL, 0, 93, 12),
(@lastDosVer, 0, 'ValidFrom',         'validFrom', 5, 93, 12),
(@lastDosVer, 0, 'ValidTo',             'validTo', 6, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('doseringsforslag', 'drug', 1, 'DosageDrug', NOW());
SET @lastDosDrug := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='doseringsforslag' AND datatype='drug' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDosDrug, 1, 'DosageDrugPID',              NULL, 0, -5, NULL),
(@lastDosDrug, 0, 'releaseNumber',   'releaseNumber', 1, -5, NULL),
(@lastDosDrug, 0, 'drugId',                 'drugId', 2, -5, NULL),
(@lastDosDrug, 0, 'dosageUnitCode', 'dosageUnitCode', 4, -5, NULL),
(@lastDosDrug, 0, 'drugName',             'drugName', 3, 12, NULL),
(@lastDosDrug, 0, 'ModifiedDate',               NULL, 0, 93, 12),
(@lastDosDrug, 0, 'ValidFrom',           'validFrom', 5, 93, 12),
(@lastDosDrug, 0, 'ValidTo',               'validTo', 6, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('doseringsforslag', 'drugdosagestructurerelation', 1, 'DrugDosageStructureRelation', NOW());
SET @lastDosDrugStr := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='doseringsforslag' AND datatype='drugdosagestructurerelation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDosDrugStr, 1, 'DrugDosageStructureRelationPID',                 NULL, 0, -5, NULL),
(@lastDosDrugStr, 0, 'id',                                             'id', 1, 12, NULL),
(@lastDosDrugStr, 0, 'drugId',                                     'drugId', 2, -5, NULL),
(@lastDosDrugStr, 0, 'dosageStructureCode',           'dosageStructureCode', 4, -5, NULL),
(@lastDosDrugStr, 0, 'releaseNumber',                       'releaseNumber', 3, -5, NULL),
(@lastDosDrugStr, 0, 'ModifiedDate',                                   NULL, 0, 93, 12),
(@lastDosDrugStr, 0, 'ValidFrom',                               'validFrom', 5, 93, 12),
(@lastDosDrugStr, 0, 'ValidTo',                                   'validTo', 6, 93, 12);

-- ---------------------------------------------------------------------------------------------------------------------
-- SOR

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('sor', 'apotek', 1, 'Apotek', NOW());
SET @lastApot := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sor' AND datatype='apotek' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastApot, 1, 'ApotekPID',                           NULL, 0, -5, NULL),
(@lastApot, 0, 'SorNummer',                    'sorNummer', 1, -5, NULL),
(@lastApot, 0, 'ApotekNummer',              'apotekNummer', 2, -5, NULL),
(@lastApot, 0, 'FilialNummer',              'filialNummer', 3, -5, NULL),
(@lastApot, 0, 'EanLokationsnummer',  'eanLokationsnummer', 4, -5, NULL),
(@lastApot, 0, 'cvr',                                'cvr', 5, -5, NULL),
(@lastApot, 0, 'pcvr',                              'pcvr', 6, -5, NULL),
(@lastApot, 0, 'Navn',                              'navn', 7, 12, NULL),
(@lastApot, 0, 'Telefon',                        'telefon', 8, 12, NULL),
(@lastApot, 0, 'Vejnavn',                        'vejnavn', 9, 12, NULL),
(@lastApot, 0, 'Postnummer',                  'postnummer',10, 12, NULL),
(@lastApot, 0, 'Bynavn',                          'bynavn',11, 12, NULL),
(@lastApot, 0, 'Email',                            'email',12, 12, NULL),
(@lastApot, 0, 'Www',                                'www',13, 12, NULL),
(@lastApot, 0, 'ModifiedDate',                        NULL, 0, 93, 12),
(@lastApot, 0, 'ValidFrom',                    'validFrom',14, 93, 12),
(@lastApot, 0, 'ValidTo',                        'validTo',15, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('sor', 'praksis', 1, 'Praksis', NOW());
SET @lastPraks := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sor' AND datatype='praksis' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPraks, 1, 'PraksisPID',                         NULL, 0, -5, NULL),
(@lastPraks, 0, 'SorNummer',                   'sorNummer', 1, -5, NULL),
(@lastPraks, 0, 'EanLokationsnummer', 'eanLokationsnummer', 2, -5, NULL),
(@lastPraks, 0, 'RegionCode',                 'regionCode', 3, -5, NULL),
(@lastPraks, 0, 'Navn',                             'navn', 4, 12, NULL),
(@lastPraks, 0, 'ModifiedDate',                       NULL, 0, 93, 12),
(@lastPraks, 0, 'ValidFrom',                   'validFrom', 5, 93, 12),
(@lastPraks, 0, 'ValidTo',                       'validTo', 6, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('sor', 'sygehus', 1, 'Sygehus', NOW());
SET @lastSygehus := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sor' AND datatype='sygehus' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSygehus, 1, 'SygeHusPID',                         NULL, 0, -5, NULL),
(@lastSygehus, 0, 'SorNummer',                   'sorNummer', 1, -5, NULL),
(@lastSygehus, 0, 'EanLokationsnummer', 'eanLokationsnummer', 2, -5, NULL),
(@lastSygehus, 0, 'Nummer',                         'nummer', 3, 12, NULL),
(@lastSygehus, 0, 'Telefon',                       'telefon', 5, 12, NULL),
(@lastSygehus, 0, 'Navn',                             'navn', 4, 12, NULL),
(@lastSygehus, 0, 'Vejnavn',                       'vejnavn', 6, 12, NULL),
(@lastSygehus, 0, 'Postnummer',                 'postnummer', 7, 12, NULL),
(@lastSygehus, 0, 'Bynavn',                         'bynavn', 8, 12, NULL),
(@lastSygehus, 0, 'Email',                           'email', 9, 12, NULL),
(@lastSygehus, 0, 'Www',                               'www',10, 12, NULL),
(@lastSygehus, 0, 'ModifiedDate',                       NULL, 0, 93, 12),
(@lastSygehus, 0, 'ValidFrom',                   'validFrom',11, 93, 12),
(@lastSygehus, 0, 'ValidTo',                       'validTo',12, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('sor', 'sygehusafdeling', 1, 'SygehusAfdeling', NOW());
SET @lastSygehusAfd := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sor' AND datatype='sygehusafdeling' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSygehusAfd, 1, 'SygeHusAfdelingPID',                               NULL, 0, -5, NULL),
(@lastSygehusAfd, 0, 'SorNummer',                                 'sorNummer', 1, -5, NULL),
(@lastSygehusAfd, 0, 'EanLokationsnummer',               'eanLokationsnummer', 5, -5, NULL),
(@lastSygehusAfd, 0, 'Nummer',                                       'nummer',10, 12, NULL),
(@lastSygehusAfd, 0, 'Navn',                                           'navn',11, 12, NULL),
(@lastSygehusAfd, 0, 'SygehusSorNummer',                   'sygehusSorNummer', 2, -5, NULL),
(@lastSygehusAfd, 0, 'OverAfdelingSorNummer',         'overafdelingSorNummer', 3, -5, NULL),
(@lastSygehusAfd, 0, 'UnderlagtSygehusSorNummer', 'underlagtSygehusSorNummer', 4, -5, NULL),
(@lastSygehusAfd, 0, 'AfdelingTypeKode',                   'afdelingTypeKode', 6, -5, NULL),
(@lastSygehusAfd, 0, 'AfdelingTypeTekst',                 'afdelingTypeTekst', 7, 12, NULL),
(@lastSygehusAfd, 0, 'HovedSpecialeKode',                 'hovedSpecialeKode', 8, 12, NULL),
(@lastSygehusAfd, 0, 'HovedSpecialeTekst',               'hovedSpecialeTekst', 9, 12, NULL),
(@lastSygehusAfd, 0, 'Telefon',                                     'telefon',12, 12, NULL),
(@lastSygehusAfd, 0, 'Vejnavn',                                     'vejnavn',13, 12, NULL),
(@lastSygehusAfd, 0, 'Postnummer',                               'postnummer',14, 12, NULL),
(@lastSygehusAfd, 0, 'Bynavn',                                       'bynavn',15, 12, NULL),
(@lastSygehusAfd, 0, 'Email',                                         'email',16, 12, NULL),
(@lastSygehusAfd, 0, 'Www',                                             'www',17, 12, NULL),
(@lastSygehusAfd, 0, 'ModifiedDate',                                     NULL, 0, 93, 12),
(@lastSygehusAfd, 0, 'ValidFrom',                                 'validFrom',18, 93, 12),
(@lastSygehusAfd, 0, 'ValidTo',                                     'validTo',19, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('sor', 'yder', 1, 'Yder', NOW());
SET @lastYder := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sor' AND datatype='yder' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastYder, 1, 'YderPID',                            NULL, 0, -5, NULL),
(@lastYder, 0, 'Nummer',                         'nummer', 4, -5, NULL),
(@lastYder, 0, 'SorNummer',                   'sorNummer', 1, -5, NULL),
(@lastYder, 0, 'PraksisSorNummer',     'praksisSorNummer', 2, -5, NULL),
(@lastYder, 0, 'EanLokationsnummer', 'eanLokationsnummer', 3, -5, NULL),
(@lastYder, 0, 'Telefon',                       'telefon', 6, 12, NULL),
(@lastYder, 0, 'Navn',                             'navn', 5, 12, NULL),
(@lastYder, 0, 'Vejnavn',                       'vejnavn', 7, 12, NULL),
(@lastYder, 0, 'Postnummer',                 'postnummer', 8, 12, NULL),
(@lastYder, 0, 'Bynavn',                         'bynavn', 9, 12, NULL),
(@lastYder, 0, 'Email',                           'email',10, 12, NULL),
(@lastYder, 0, 'Www',                               'www',11, 12, NULL),
(@lastYder, 0, 'HovedSpecialeKode',   'hovedSpecialeKode',12, 12, NULL),
(@lastYder, 0, 'HovedSpecialeTekst', 'hovedSpecialeTekst',13, 12, NULL),
(@lastYder, 0, 'ModifiedDate',                       NULL, 0, 93, 12),
(@lastYder, 0, 'ValidFrom',                   'validFrom',14, 93, 12),
(@lastYder, 0, 'ValidTo',                       'validTo',15, 93, 12);

-- ---------------------------------------------------------------------------------------------------------------------
-- Tilskudsblanket
INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('tilskudsblanket', 'blanket', 1, 'Tilskudsblanket', NOW());
SET @lastBlanket := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='tilskudsblanket' AND datatype='blanket' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBlanket, 1, 'PID',                              NULL, 0, -5, NULL),
(@lastBlanket, 0, 'BlanketId',                 'BlanketId', 1, -5, NULL),
(@lastBlanket, 0, 'BlanketTekst',           'BlanketTekst', 2, 12, NULL),
(@lastBlanket, 0, 'ModifiedDate',           'ModifiedDate', 3, 93, 12),
(@lastBlanket, 0, 'ValidFrom',                 'ValidFrom', 4, 93, 12),
(@lastBlanket, 0, 'ValidTo',                     'ValidTo', 5, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('tilskudsblanket', 'forhoejettakst', 1, 'TilskudForhoejetTakst', NOW());
SET @lastForTakst := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='tilskudsblanket' AND datatype='forhoejettakst' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastForTakst, 1, 'PID',                              NULL, 0, -5, NULL),
(@lastForTakst, 0, 'Varenummer',               'Varenummer', 1, -5, NULL),
(@lastForTakst, 0, 'Navn',                           'Navn', 1, 12, NULL),
(@lastForTakst, 0, 'Form',                           'Form', 2, 12, NULL),
(@lastForTakst, 0, 'FormTekst',                 'FormTekst', 3, 12, NULL),
(@lastForTakst, 0, 'ATCkode',                     'ATCkode', 4, 12, NULL),
(@lastForTakst, 0, 'Styrke',                       'Styrke', 5, 12, NULL),
(@lastForTakst, 0, 'DrugID',                       'DrugID', 6, -5, NULL),
(@lastForTakst, 0, 'PakningsTekst',         'PakningsTekst', 7, 12, NULL),
(@lastForTakst, 0, 'Udlevering',               'Udlevering', 8, 12, NULL),
(@lastForTakst, 0, 'Tilskudstype',           'Tilskudstype', 9, 12, NULL),
(@lastForTakst, 0, 'ModifiedDate',           'ModifiedDate',10, 93, 12),
(@lastForTakst, 0, 'ValidFrom',                 'ValidFrom',11, 93, 12),
(@lastForTakst, 0, 'ValidTo',                     'ValidTo',12, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('tilskudsblanket', 'blanketforhoejet', 1, 'TilskudsblanketForhoejet', NOW());
SET @lastBlanketForH := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='tilskudsblanket' AND datatype='blanketforhoejet' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBlanketForH, 1, 'PID',                              NULL, 0, -5, NULL),
(@lastBlanketForH, 0, 'BlanketId',                 'BlanketId', 1, -5, NULL),
(@lastBlanketForH, 0, 'DrugId',                       'DrugId', 2, 12, NULL),
(@lastBlanketForH, 0, 'ModifiedDate',           'ModifiedDate', 3, 93, 12),
(@lastBlanketForH, 0, 'ValidFrom',                 'ValidFrom', 4, 93, 12),
(@lastBlanketForH, 0, 'ValidTo',                     'ValidTo', 5, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('tilskudsblanket', 'blanketenkelt', 1, 'TilskudsblanketEnkelt', NOW());
SET @lastBlanketEnkelt := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='tilskudsblanket' AND datatype='blanketenkelt' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBlanketEnkelt, 1, 'PID',                      NULL, 0, -5, NULL),
(@lastBlanketEnkelt, 0, 'BlanketId',         'BlanketId', 1, -5, NULL),
(@lastBlanketEnkelt, 0, 'Genansoegning', 'Genansoegning', 2, -5, NULL),
(@lastBlanketEnkelt, 0, 'Navn',                   'Navn', 3, 12, NULL),
(@lastBlanketEnkelt, 0, 'Form',                   'Form', 4, 12, NULL),
(@lastBlanketEnkelt, 0, 'Id',                       'Id', 5, 12, NULL),
(@lastBlanketEnkelt, 0, 'ModifiedDate',   'ModifiedDate', 6, 93, 12),
(@lastBlanketEnkelt, 0, 'ValidFrom',         'ValidFrom', 7, 93, 12),
(@lastBlanketEnkelt, 0, 'ValidTo',             'ValidTo', 8, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('tilskudsblanket', 'blanketterminal', 1, 'TilskudsblanketTerminal', NOW());
SET @lastBlanketTerminal := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='tilskudsblanket' AND datatype='blanketterminal' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBlanketTerminal, 1, 'PID',                      NULL, 0, -5, NULL),
(@lastBlanketTerminal, 0, 'Id',                       'Id', 1, 12, NULL),
(@lastBlanketTerminal, 0, 'BlanketId',         'BlanketId', 2, -5, NULL),
(@lastBlanketTerminal, 0, 'ModifiedDate',   'ModifiedDate', 3, 93, 12),
(@lastBlanketTerminal, 0, 'ValidFrom',         'ValidFrom', 4, 93, 12),
(@lastBlanketTerminal, 0, 'ValidTo',             'ValidTo', 5, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('tilskudsblanket', 'blanketkroniker', 1, 'TilskudsblanketKroniker', NOW());
SET @lastBlanketKro := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='tilskudsblanket' AND datatype='blanketkroniker' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBlanketKro, 1, 'PID',                      NULL, 0, -5, NULL),
(@lastBlanketKro, 0, 'BlanketId',         'BlanketId', 1, -5, NULL),
(@lastBlanketKro, 0, 'Genansoegning', 'Genansoegning', 2, -5, NULL),
(@lastBlanketKro, 0, 'ModifiedDate',   'ModifiedDate', 3, 93, 12),
(@lastBlanketKro, 0, 'ValidFrom',         'ValidFrom', 4, 93, 12),
(@lastBlanketKro, 0, 'ValidTo',             'ValidTo', 5, 93, 12);

-- ---------------------------------------------------------------------------------------------------------------------
-- Bemyndigelse
INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('bemyndigelsesservice', 'bemyndigelse', 1, 'Bemyndigelse', NOW());
SET @lastBem := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBem, 1, 'PID',                              NULL, 0, -5, NULL),
(@lastBem, 0, 'kode',                           'kode', 1, 12, NULL),
(@lastBem, 0, 'bemyndigende_cpr',   'bemyndigende_cpr', 2, 12, NULL),
(@lastBem, 0, 'bemyndigede_cpr',     'bemyndigede_cpr', 3, 12, NULL),
(@lastBem, 0, 'bemyndigede_cvr',     'bemyndigede_cvr', 4, 12, NULL),
(@lastBem, 0, 'system',                       'system', 5, 12, NULL),
(@lastBem, 0, 'arbejdsfunktion',     'arbejdsfunktion', 6, 12, NULL),
(@lastBem, 0, 'rettighed',                 'rettighed', 7, 12, NULL),
(@lastBem, 0, 'status',                       'status', 8, 12, NULL),
(@lastBem, 0, 'godkendelses_dato', 'godkendelses_dato', 9, 12, NULL),
(@lastBem, 0, 'oprettelses_dato',   'oprettelses_dato',10, 12, NULL),
(@lastBem, 0, 'modificeret_dato',   'modificeret_dato',11, 12, NULL),
(@lastBem, 0, 'gyldig_fra_dato',     'gyldig_fra_dato',12, 12, NULL),
(@lastBem, 0, 'gyldig_til_dato',     'gyldig_til_dato',13, 12, NULL),
(@lastBem, 0, 'ModifiedDate',                     NULL, 0, 93, 12),
(@lastBem, 0, 'ValidFrom',                 'ValidFrom',14, 93, 12),
(@lastBem, 0, 'ValidTo',                     'ValidTo',15, 93, 12);

-- ---------------------------------------------------------------------------------------------------------------------
-- Yderregister

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('yderregister', 'yder', 1, 'Yderregister', NOW());
SET @lastYderMap := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='yderregister' AND datatype='yder' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastYderMap, 1, 'PID',        NULL,             0,  -5, NULL),
(@lastYderMap, 0, 'Id',         'Id',             1,  12, 32),
(@lastYderMap, 0, 'HistIdYder', 'HistIdYder',     2,  12, 16),
(@lastYderMap, 0, 'AmtKodeYder', 'AmtKodeYder',   3,  12, 2),
(@lastYderMap, 0, 'AmtTxtYder', 'AmtTxtYder',     4,  12, 60),
(@lastYderMap, 0, 'YdernrYder', 'YdernrYder',     5,  12, 6),
(@lastYderMap, 0, 'PrakBetegn', 'PrakBetegn',     6,  12, 50),
(@lastYderMap, 0, 'AdrYder', 'AdrYder',           7,  12, 50),
(@lastYderMap, 0, 'PostnrYder', 'PostnrYder',     8,  12, 4),
(@lastYderMap, 0, 'PostdistYder', 'PostdistYder', 9,  12, 20),
(@lastYderMap, 0, 'TilgDatoYder', 'TilgDatoYder', 10, 12, 8),
(@lastYderMap, 0, 'AfgDatoYder', 'AfgDatoYder',   11, 12, 8),
(@lastYderMap, 0, 'HvdSpecKode', 'HvdSpecKode',   12, 12, 2),
(@lastYderMap, 0, 'HvdSpecTxt', 'HvdSpecTxt',     13, 12, 60),
(@lastYderMap, 0, 'HvdTlf', 'HvdTlf',             14, 12, 8),
(@lastYderMap, 0, 'EmailYder', 'EmailYder',       15, 12, 50),
(@lastYderMap, 0, 'WWW', 'WWW',                   16, 12, 78),
(@lastYderMap, 0, 'ModifiedDate', 'ModifiedDate', 17, 93, 12),
(@lastYderMap, 0, 'ValidFrom', 'ValidFrom',       18, 93, 12),
(@lastYderMap, 0, 'ValidTo', 'ValidTo',           19, 93, 12);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('yderregister', 'person', 1, 'YderregisterPerson', NOW());
SET @lastPersonMap := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='yderregister' AND datatype='person' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPersonMap, 1, 'PID',           NULL,                0,  -5, NULL),
(@lastPersonMap, 0, 'Id',           'Id',                 1,  12, 32),
(@lastPersonMap, 0, 'HistIdPerson', 'HistIdPerson',       2,  12, 16),
(@lastPersonMap, 0, 'YdernrPerson', 'YdernrPerson',       3,  12, 6),
(@lastPersonMap, 0, 'TilgDatoPerson', 'TilgDatoPerson',   4,  12, 8),
(@lastPersonMap, 0, 'AfgDatoPerson', 'AfgDatoPerson',     5,  12, 8),
(@lastPersonMap, 0, 'CprNr', 'CprNr',                     6,  12, 10),
(@lastPersonMap, 0, 'PersonrolleKode', 'PersonrolleKode', 7,  12, 2),
(@lastPersonMap, 0, 'PersonrolleTxt', 'PersonrolleTxt',   8,  12, 60),
(@lastPersonMap, 0, 'ModifiedDate', 'ModifiedDate',       9,  93, 12),
(@lastPersonMap, 0, 'ValidFrom', 'ValidFrom',             10, 93, 12),
(@lastPersonMap, 0, 'ValidTo', 'ValidTo',                 11, 93, 12);

-- ---------------------------------------------------------------------------------------------------------------------
-- SKS

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('sks', 'institution', 1, 'Organisation', NOW());
SET @lastSksMap := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSksMap, 1, 'OrganisationPID',  NULL,                  0,  -5, NULL),
(@lastSksMap, 0, 'Navn',             'navn',                1,  12, NULL),
(@lastSksMap, 0, 'Nummer',           'nummer',              2,  12, NULL),
(@lastSksMap, 0, 'Organisationstype','organisationstype',   3,  12, NULL),
(@lastSksMap, 0, 'ModifiedDate',      NULL,                 0,  93, NULL),
(@lastSksMap, 0, 'ValidFrom',        'validFrom',           4,  93, NULL),
(@lastSksMap, 0, 'ValidTo',          'validTo',             5,  93, NULL);

-- ---------------------------------------------------------------------------------------------------------------------
-- CPR

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('cpr', 'barnrelation', 1, 'BarnRelation', NOW());
SET @lastBarnRelMap := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='cpr' AND datatype='barnrelation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBarnRelMap, 1, 'BarnRelationPID',  NULL,                  0,  -5, NULL),
(@lastBarnRelMap, 0, 'Id',               'id',                  1,  12, NULL),
(@lastBarnRelMap, 0, 'CPR',              'cpr',                 2,  12, NULL),
(@lastBarnRelMap, 0, 'BarnCPR',          'barnCPR',             3,  12, NULL),
(@lastBarnRelMap, 0, 'ModifiedDate',      NULL,                 0,  93, NULL),
(@lastBarnRelMap, 0, 'ValidFrom',        'validFrom',           4,  93, NULL),
(@lastBarnRelMap, 0, 'ValidTo',          'validTo',             5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('cpr', 'foraeldremyndighedrelation', 1, 'ForaeldreMyndighedRelation', NOW());
SET @lastForMynRelMap := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='cpr' AND datatype='foraeldremyndighedrelation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastForMynRelMap, 1, 'ForaeldreMyndighedRelationPID', NULL,    0,  -5, NULL),
(@lastForMynRelMap, 0, 'Id',                            'id',    1,  12, NULL),
(@lastForMynRelMap, 0, 'CPR',                          'cpr',    2,  12, NULL),
(@lastForMynRelMap, 0, 'TypeKode',                'typeKode',    3,  12, NULL),
(@lastForMynRelMap, 0, 'TypeTekst',              'typeTekst',    4,  12, NULL),
(@lastForMynRelMap, 0, 'RelationCpr',          'relationCpr',    5,  12, NULL),
(@lastForMynRelMap, 0, 'ModifiedDate',                  NULL,    0,  93, NULL),
(@lastForMynRelMap, 0, 'ValidFrom',              'validFrom',    6,  93, NULL),
(@lastForMynRelMap, 0, 'ValidTo',                  'validTo',    7,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('cpr', 'person', 1, 'Person', NOW());
SET @lastPersonMap := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='cpr' AND datatype='person' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPersonMap, 1, 'PersonPID',                                         NULL,  0,  -5, NULL),
(@lastPersonMap, 0, 'CPR',                                              'cpr',  1,  12, NULL),
(@lastPersonMap, 0, 'Koen',                                            'koen',  2,  12, NULL),
(@lastPersonMap, 0, 'Fornavn',                                      'fornavn',  3,  12, NULL),
(@lastPersonMap, 0, 'Mellemnavn',                                'mellemnavn',  4,  12, NULL),
(@lastPersonMap, 0, 'Efternavn',                                  'efternavn',  5,  12, NULL),
(@lastPersonMap, 0, 'CoNavn',                                        'coNavn',  6,  12, NULL),
(@lastPersonMap, 0, 'Lokalitet',                                  'lokalitet',  7,  12, NULL),
(@lastPersonMap, 0, 'Vejnavn',                                      'vejnavn',  8,  12, NULL),
(@lastPersonMap, 0, 'Bygningsnummer',                        'bygningsnummer',  9,  12, NULL),
(@lastPersonMap, 0, 'Husnummer',                                  'husnummer', 10,  12, NULL),
(@lastPersonMap, 0, 'Etage',                                          'etage', 11,  12, NULL),
(@lastPersonMap, 0, 'SideDoerNummer',                        'sideDoerNummer', 12,  12, NULL),
(@lastPersonMap, 0, 'Bynavn',                                        'bynavn', 13,  12, NULL),
(@lastPersonMap, 0, 'Postnummer',                                'postnummer', 14,  4,  NULL),
(@lastPersonMap, 0, 'PostDistrikt',                            'postdistrikt', 15,  12, NULL),
(@lastPersonMap, 0, 'Status',                                        'status', 16,  12, NULL),
(@lastPersonMap, 0, 'GaeldendeCPR',                            'gaeldendeCPR', 17,  12, NULL),
(@lastPersonMap, 0, 'foedselsdato',                            'foedselsdato', 18,  91, NULL),
(@lastPersonMap, 0, 'Stilling',                                    'stilling', 19,  12, NULL),
(@lastPersonMap, 0, 'VejKode',                                      'vejKode', 20,  4,  NULL),
(@lastPersonMap, 0, 'KommuneKode',                              'kommuneKode', 21,  4,  NULL),
(@lastPersonMap, 0, 'NavneBeskyttelseSletteDato','navnebeskyttelseslettedato', 22,  93, NULL),
(@lastPersonMap, 0, 'NavneBeskyttelseStartDato',  'navnebeskyttelsestartdato', 23,  93, NULL),
(@lastPersonMap, 0, 'ModifiedDate',                                      NULL,  0,  93, NULL),
(@lastPersonMap, 0, 'ValidFrom',                                  'validFrom', 24,  93, NULL),
(@lastPersonMap, 0, 'ValidTo',                                      'validTo', 25,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('cpr', 'umyndiggoerelsevaergerelation', 1, 'UmyndiggoerelseVaergeRelation', NOW());
SET @lastUmyn := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='cpr' AND datatype='umyndiggoerelsevaergerelation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastUmyn, 1, 'UmyndiggoerelseVaergeRelationPID',  NULL, 0,  -5, NULL),
(@lastUmyn, 0, 'Id',                                    'id', 1,  12, NULL),
(@lastUmyn, 0, 'CPR',                                  'cpr', 2,  12, NULL),
(@lastUmyn, 0, 'TypeKode',                        'typeKode', 3,  12, NULL),
(@lastUmyn, 0, 'TypeTekst',                      'typeTekst', 4,  12, NULL),
(@lastUmyn, 0, 'RelationCpr',                  'relationCpr', 5,  12, NULL),
(@lastUmyn, 0, 'RelationCprStartDato','relationCprStartDato', 6,  93, NULL),
(@lastUmyn, 0, 'VaergesNavn',                  'vaergesNavn', 7,  12, NULL),
(@lastUmyn, 0, 'VaergesNavnStartDato','vaergesNavnStartDato', 8,  93, NULL),
(@lastUmyn, 0, 'RelationsTekst1',          'relationsTekst1', 9,  12, NULL),
(@lastUmyn, 0, 'RelationsTekst2',          'relationsTekst2',10,  12, NULL),
(@lastUmyn, 0, 'RelationsTekst3',          'relationsTekst3',11,  12, NULL),
(@lastUmyn, 0, 'RelationsTekst4',          'relationsTekst4',12,  12, NULL),
(@lastUmyn, 0, 'RelationsTekst5',          'relationsTekst5',13,  12, NULL),
(@lastUmyn, 0, 'ModifiedDate',                          NULL, 0,  93, NULL),
(@lastUmyn, 0, 'ValidFrom',                      'validFrom',14,  93, NULL),
(@lastUmyn, 0, 'ValidTo',                          'validTo',15,  93, NULL);

-- ---------------------------------------------------------------------------------------------------------------------
-- Autorisation

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('autorisationsregisteret', 'autorisation', 1, 'Autorisation', NOW());
SET @lastAut := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='autorisationsregisteret' AND datatype='autorisation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastAut, 1, 'AutorisationPID',                       NULL, 0,  -5, NULL),
(@lastAut, 0, 'Autorisationsnummer',  'autorisationsnummer', 1,  12, NULL),
(@lastAut, 0, 'CPR',                                  'cpr', 2,  12, NULL),
(@lastAut, 0, 'Fornavn',                          'fornavn', 3,  12, NULL),
(@lastAut, 0, 'Efternavn',                      'efternavn', 4,  12, NULL),
(@lastAut, 0, 'UddannelsesKode',          'uddannelseskode', 5,  4,  NULL),
(@lastAut, 0, 'ModifiedDate',                          NULL, 0,  93, NULL),
(@lastAut, 0, 'ValidFrom',                      'validFrom', 6,  93, NULL),
(@lastAut, 0, 'ValidTo',                          'validTo', 7,  93, NULL);

-- ---------------------------------------------------------------------------------------------------------------------
-- Sikrede

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('sikrede', 'sikrede', 1, 'Sikrede', NOW());
SET @lastSik := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSik, 1, 'PID',                                       NULL, 0,  -5, NULL),
(@lastSik, 0, 'CPRnr',                                        'CPRnr', 2,  12, 10),
(@lastSik, 0, 'SYdernr',                                    'SYdernr', 3,  12, 6),
(@lastSik, 0, 'SIkraftDatoYder',                    'SIkraftDatoYder', 4,  12, 8),
(@lastSik, 0, 'SRegDatoYder',                          'SRegDatoYder', 5,  12, 8),
(@lastSik, 0, 'SSikrGrpKode',                          'SSikrGrpKode', 6,  12, 1),
(@lastSik, 0, 'SIkraftDatoGrp',                      'SIkraftDatoGrp', 7,  12, 8),
(@lastSik, 0, 'SRegDatoGrp',                            'SRegDatoGrp', 8,  12, 8),
(@lastSik, 0, 'SSikrKomKode',                          'SSikrKomKode', 9,  12, 3),
(@lastSik, 0, 'SIkraftDatoKomKode',              'SIkraftDatoKomKode',10,  12, 8),
(@lastSik, 0, 'SYdernrGl',                                'SYdernrGl',11,  12, 6),
(@lastSik, 0, 'SIkraftDatoYderGl',                'SIkraftDatoYderGl',12,  12, 8),
(@lastSik, 0, 'SRegDatoYderGl',                      'SRegDatoYderGl',13,  12, 8),
(@lastSik, 0, 'SSikrGrpKodeGl',                      'SSikrGrpKodeGl',14,  12, 1),
(@lastSik, 0, 'SIkraftDatoGrpGl',                  'SIkraftDatoGrpGl',15,  12, 8),
(@lastSik, 0, 'SRegDatoGrpGl',                        'SRegDatoGrpGl',16,  12, 8),
(@lastSik, 0, 'SYdernrFrem',                            'SYdernrFrem',17,  12, 6),
(@lastSik, 0, 'SIkraftDatoYderFrem',            'SIkraftDatoYderFrem',18,  12, 8),
(@lastSik, 0, 'SRegDatoYderFrem',                  'SRegDatoYderFrem',19,  12, 8),
(@lastSik, 0, 'SSikrGrpKodeFrem',                  'SSikrGrpKodeFrem',20,  12, 1),
(@lastSik, 0, 'SIkraftDatoGrpFrem',              'SIkraftDatoGrpFrem',21,  12, 8),
(@lastSik, 0, 'SRegDatoGrpFrem',                    'SRegDatoGrpFrem',22,  12, 8),
(@lastSik, 0, 'SKon',                                          'SKon',23,  12, 1),
(@lastSik, 0, 'SAlder',                                      'SAlder',24,  12, 3),
(@lastSik, 0, 'SFolgerskabsPerson',              'SFolgerskabsPerson',25,  12, 10),
(@lastSik, 0, 'SStatus',                                    'SStatus',26,  12, 2),
(@lastSik, 0, 'SBevisDato',                              'SBevisDato',27,  12, 8),
(@lastSik, 0, 'PNavn',                                        'PNavn',28,  12, 34),
(@lastSik, 0, 'SBSStatsborgerskabKode',      'SBSStatsborgerskabKode',29,  12, 2),
(@lastSik, 0, 'SBSStatsborgerskab',              'SBSStatsborgerskab',30,  12, 47),
(@lastSik, 0, 'SSKAdrLinie1',                          'SSKAdrLinie1',31,  12, 40),
(@lastSik, 0, 'SSKAdrLinie2',                          'SSKAdrLinie2',32,  12, 40),
(@lastSik, 0, 'SSKBopelsLand',                        'SSKBopelsLand',33,  12, 40),
(@lastSik, 0, 'SSKBopelsLAndKode',                'SSKBopelsLAndKode',34,  12, 2),
(@lastSik, 0, 'SSKEmailAdr',                            'SSKEmailAdr',35,  12, 50),
(@lastSik, 0, 'SSKFamilieRelation',              'SSKFamilieRelation',36,  12, 10),
(@lastSik, 0, 'SSKFodselsdato',                      'SSKFodselsdato',37,  12, 10),
(@lastSik, 0, 'SSKGyldigFra',                          'SSKGyldigFra',38,  12, 10),
(@lastSik, 0, 'SSKGyldigTil',                          'SSKGyldigTil',39,  12, 10),
(@lastSik, 0, 'SSKMobilNr',                              'SSKMobilNr',40,  12, 20),
(@lastSik, 0, 'SSKPostNrBy',                            'SSKPostNrBy',41,  12, 40),
(@lastSik, 0, 'SSLForsikringsinstans',        'SSLForsikringsinstans',42,  12, 21),
(@lastSik, 0, 'SSLForsikringsinstansKode','SSLForsikringsinstansKode',43,  12, 10),
(@lastSik, 0, 'SSLForsikringsnr',                  'SSLForsikringsnr',44,  12, 15),
(@lastSik, 0, 'SSLGyldigFra',                          'SSLGyldigFra',45,  12, 10),
(@lastSik, 0, 'SSLGyldigTil',                          'SSLGyldigTil',56,  12, 10),
(@lastSik, 0, 'SSLSocSikretLand',                  'SSLSocSikretLand',57,  12, 47),
(@lastSik, 0, 'SSLSocSikretLandKode',          'SSLSocSikretLandKode',58,  12, 2),
(@lastSik, 0, 'ModifiedDate',                                    NULL, 0,  93, NULL),
(@lastSik, 0, 'ValidFrom',                                'validFrom',59,  93, NULL),
(@lastSik, 0, 'ValidTo',                                    'validTo',60,  93, NULL);

-- ---------------------------------------------------------------------------------------------------------------------
-- Taksten
INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'administrationsvej', 1, 'Administrationsvej', NOW());
SET @lastAdmVej := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='administrationsvej' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastAdmVej, 1, 'AdministrationsvejPID',             NULL, 0,  -5, NULL),
(@lastAdmVej, 0, 'AdministrationsvejKode',            'id', 1,  12, NULL),
(@lastAdmVej, 0, 'AdministrationsvejTekst',        'tekst', 2,  12, NULL),
(@lastAdmVej, 0, 'AdministrationsvejKortTekst','kortTekst', 3,  12, NULL),
(@lastAdmVej, 0, 'ModifiedDate',                      NULL, 0,  93, NULL),
(@lastAdmVej, 0, 'ValidFrom',                  'validFrom', 4,  93, NULL),
(@lastAdmVej, 0, 'ValidTo',                      'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'atc', 1, 'ATC', NOW());
SET @lastAtc := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='atc' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastAtc, 1, 'ATCPID',              NULL, 0,  -5, NULL),
(@lastAtc, 0, 'ATC',               'kode', 1,  12, NULL),
(@lastAtc, 0, 'ATCTekst',         'tekst', 2,  12, NULL),
(@lastAtc, 0, 'ModifiedDate',        NULL, 0,  93, NULL),
(@lastAtc, 0, 'ValidFrom',    'validFrom', 3,  93, NULL),
(@lastAtc, 0, 'ValidTo',        'validTo', 4,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'beregningsregler', 1, 'Beregningsregler', NOW());
SET @lastBeregnRegel := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='beregningsregler' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastBeregnRegel, 1, 'BeregningsreglerPID',    NULL, 0,  -5, NULL),
(@lastBeregnRegel, 0, 'Kode',                 'kode', 1,  12, NULL),
(@lastBeregnRegel, 0, 'Tekst',               'tekst', 2,  12, NULL),
(@lastBeregnRegel, 0, 'ModifiedDate',           NULL, 0,  93, NULL),
(@lastBeregnRegel, 0, 'ValidFrom',       'validFrom', 3,  93, NULL),
(@lastBeregnRegel, 0, 'ValidTo',           'validTo', 4,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'dosering', 1, 'Dosering', NOW());
SET @lastDos := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='dosering' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDos, 1, 'DoseringPID',    NULL, 0,  -5, NULL),
(@lastDos, 0, 'DoseringKode',                        'kode', 1,  -5, NULL),
(@lastDos, 0, 'DoseringTekst',                      'tekst', 2,  12, NULL),
(@lastDos, 0, 'DoseringKortTekst',              'kortTekst', 3,  12, NULL),
(@lastDos, 0, 'DoseringstekstLinie1',   'beskrivelseLinje1', 4,  12, NULL),
(@lastDos, 0, 'DoseringstekstLinie2',   'beskrivelseLinje2', 5,  12, NULL),
(@lastDos, 0, 'DoseringstekstLinie3',   'beskrivelseLinje3', 6,  12, NULL),
(@lastDos, 0, 'antalEnhederPrDoegn',  'antalEnhederPrDoegn', 7,  6, NULL),
(@lastDos, 0, 'aktiv',                              'aktiv', 8,  16, NULL),
(@lastDos, 0, 'ModifiedDate',                          NULL, 0,  93, NULL),
(@lastDos, 0, 'ValidFrom',                      'validFrom', 9,  93, NULL),
(@lastDos, 0, 'ValidTo',                          'validTo',10,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'drugsdosagesrelation', 1, 'LaegemiddelDoseringRef', NOW());
SET @lastDrugDos := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='drugsdosagesrelation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDrugDos, 1, 'LaegemiddelDoseringRefPID',             NULL, 0,  -5, NULL),
(@lastDrugDos, 0, 'CID',                                   'id', 1,  12, NULL),
(@lastDrugDos, 0, 'DrugId',                            'drugId', 2,  -5, NULL),
(@lastDrugDos, 0, 'DoseringKode',                  'dosageCode', 3,  -5, NULL),
(@lastDrugDos, 0, 'ModifiedDate',                          NULL, 0,  93, NULL),
(@lastDrugDos, 0, 'ValidFrom',                      'validFrom', 4,  93, NULL),
(@lastDrugDos, 0, 'ValidTo',                          'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'drug', 1, 'Laegemiddel', NOW());
SET @lastLaegemiddel := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='drug' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastLaegemiddel, 1, 'LaegemiddelPID',             NULL, 0,  -5, NULL),
(@lastLaegemiddel, 0, 'DrugID',                                                      'id', 1,  -5, NULL),
(@lastLaegemiddel, 0, 'DrugName',                                                  'nave', 2,  12, NULL),
(@lastLaegemiddel, 0, 'FormKode',                                              'formKode', 3,  12, NULL),
(@lastLaegemiddel, 0, 'FormTekst',                                            'formTekst', 4,  12, NULL),
(@lastLaegemiddel, 0, 'StyrkeTekst',                                        'styrkeTekst', 5,  12, NULL),
(@lastLaegemiddel, 0, 'StyrkeNumerisk',                                          'styrke', 6,   3, NULL),
(@lastLaegemiddel, 0, 'StyrkeEnhed',                                        'styrkeenhed', 7,  12, NULL),
(@lastLaegemiddel, 0, 'ATCKode',                                                    'atc', 8,  12, NULL),
(@lastLaegemiddel, 0, 'ATCTekst',                                              'atcTekst', 9,  12, NULL),
(@lastLaegemiddel, 0, 'Dosisdispenserbar',                            'dosisdispenserbar',10,  16, NULL),
(@lastLaegemiddel, 0, 'Varetype',                                              'varetype',11,  12, NULL),
(@lastLaegemiddel, 0, 'Varedeltype',                                        'varedeltype',12,  12, NULL),
(@lastLaegemiddel, 0, 'AlfabetSekvensplads',                        'alfabetSekvensplads',13,  12, NULL),
(@lastLaegemiddel, 0, 'SpecNummer',                                          'specNummer',14,  -5, NULL),
(@lastLaegemiddel, 0, 'LaegemiddelformTekst',                      'LaegemiddelformTekst',15,  12, NULL),
(@lastLaegemiddel, 0, 'KodeForYderligereFormOplysn',        'kodeForYderligereFormOplysn',16,  12, NULL),
(@lastLaegemiddel, 0, 'Trafikadvarsel',                                  'trafikadvarsel',17,  16, NULL),
(@lastLaegemiddel, 0, 'Substitution',                                      'substitution',18,  12, NULL),
(@lastLaegemiddel, 0, 'LaegemidletsSubstitutionsgruppe','laegemidletsSubstitutionsgruppe',19,  12, NULL),
(@lastLaegemiddel, 0, 'DatoForAfregistrAfLaegemiddel',    'datoForAfregistrAfLaegemiddel',20,  91, NULL),
(@lastLaegemiddel, 0, 'Karantaenedato',                                  'karantaenedato',21,  91, NULL),
(@lastLaegemiddel, 0, 'AdministrationsvejKode',                  'administrationsvejKode',22,  12, NULL),
(@lastLaegemiddel, 0, 'MTIndehaverKode',                                'mtIndehaverKode',23,  -5, NULL),
(@lastLaegemiddel, 0, 'RepraesentantDistributoerKode',    'repraesentantDistributoerKode',24,  -5, NULL),
(@lastLaegemiddel, 0, 'ModifiedDate',                                                NULL, 0,  93, NULL),
(@lastLaegemiddel, 0, 'ValidFrom',                                            'validFrom',25,  93, NULL),
(@lastLaegemiddel, 0, 'ValidTo',                                                'validTo',26,  93, NULL);


INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'emballagetypekoder', 1, 'EmballagetypeKoder', NOW());
SET @lastEmbalKode := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='emballagetypekoder' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastEmbalKode, 1, 'EmballagetypeKoderPID',             NULL, 0,  -5, NULL),
(@lastEmbalKode, 0, 'Kode',                                  'id', 1,  12, NULL),
(@lastEmbalKode, 0, 'Tekst',                              'tekst', 2,  12, NULL),
(@lastEmbalKode, 0, 'KortTekst',                      'kortTekst', 3,  12, NULL),
(@lastEmbalKode, 0, 'ModifiedDate',                          NULL, 0,  93, NULL),
(@lastEmbalKode, 0, 'ValidFrom',                      'validFrom', 4,  93, NULL),
(@lastEmbalKode, 0, 'ValidTo',                          'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'enhedspriser', 1, 'Enhedspriser', NOW());
SET @lastEnhedsP := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='enhedspriser' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastEnhedsP, 1, 'EnhedspriserPID',                NULL, 0,  -5, NULL),
(@lastEnhedsP, 0, 'Varenummer',             'varenummer', 1,  -5, NULL),
(@lastEnhedsP, 0, 'DrugID',                     'drugId', 2,  -5, NULL),
(@lastEnhedsP, 0, 'PrisPrEnhed',           'prisPrEnhed', 3,  -5, NULL),
(@lastEnhedsP, 0, 'PrisPrDDD',               'prisPrDDD', 4,  -5, NULL),
(@lastEnhedsP, 0, 'BilligstePakning', 'billigstePakning', 5,  12, NULL),
(@lastEnhedsP, 0, 'ModifiedDate',                   NULL, 0,  93, NULL),
(@lastEnhedsP, 0, 'ValidFrom',               'validFrom', 6,  93, NULL),
(@lastEnhedsP, 0, 'ValidTo',                   'validTo', 7,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'firma', 1, 'Firma', NOW());
SET @lastFirma := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='firma' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastFirma, 1, 'FirmaPID',                                NULL, 0,  -5, NULL),
(@lastFirma, 0, 'Firmanummer',                    'firmanummer', 1,  -5, NULL),
(@lastFirma, 0, 'FirmamaerkeKort',        'firmamaerkeKortNavn', 2,  12, NULL),
(@lastFirma, 0, 'FirmamaerkeLangtNavn',  'firmamaerkeLangtNavn', 3,  12, NULL),
(@lastFirma, 0, 'ParallelimportoerKode','parallelimportoerKode', 4,  12, NULL),
(@lastFirma, 0, 'ModifiedDate',                            NULL, 0,  93, NULL),
(@lastFirma, 0, 'ValidFrom',                        'validFrom', 5,  93, NULL),
(@lastFirma, 0, 'ValidTo',                            'validTo', 6,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'formbetegnelse', 1, 'Formbetegnelse', NOW());
SET @lastFormBet := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='formbetegnelse' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastFormBet, 1, 'FormbetegnelsePID',       NULL, 0,  -5, NULL),
(@lastFormBet, 0, 'Kode',                    'id', 1,  12, NULL),
(@lastFormBet, 0, 'Tekst',                'tekst', 2,  12, NULL),
(@lastFormBet, 0, 'Aktiv',                'aktiv', 3,  16, NULL),
(@lastFormBet, 0, 'ModifiedDate',            NULL, 0,  93, NULL),
(@lastFormBet, 0, 'ValidFrom',        'validFrom', 4,  93, NULL),
(@lastFormBet, 0, 'ValidTo',            'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'indholdsstoffer', 1, 'Indholdsstoffer', NOW());
SET @lastIndStof := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='indholdsstoffer' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastIndStof, 1, 'IndholdsstofferPID',         NULL, 0,  -5, NULL),
(@lastIndStof, 0, 'CID',                        'id', 1,  12, NULL),
(@lastIndStof, 0, 'DrugID',                 'drugId', 2,  -5, NULL),
(@lastIndStof, 0, 'Varenummer',         'varenummer', 3,  -5, NULL),
(@lastIndStof, 0, 'Stofklasse',         'stofklasse', 4,  12, NULL),
(@lastIndStof, 0, 'Substans',             'substans', 5,  12, NULL),
(@lastIndStof, 0, 'Substansgruppe', 'substansgruppe', 6,  12, NULL),
(@lastIndStof, 0, 'ModifiedDate',               NULL, 0,  93, NULL),
(@lastIndStof, 0, 'ValidFrom',           'validFrom', 7,  93, NULL),
(@lastIndStof, 0, 'ValidTo',               'validTo', 8,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'indikation', 1, 'Indikation', NOW());
SET @lastIndik := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='indikation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastIndik, 1, 'IndikationPID',                   NULL, 0,  -5, NULL),
(@lastIndik, 0, 'IndikationKode',                  'id', 1,  -5, NULL),
(@lastIndik, 0, 'IndikationTekst',              'tekst', 2,  12, NULL),
(@lastIndik, 0, 'IndikationstekstLinie1', 'tekstLinje1', 3,  12, NULL),
(@lastIndik, 0, 'IndikationstekstLinie2', 'tekstLinje2', 4,  12, NULL),
(@lastIndik, 0, 'IndikationstekstLinie3', 'tekstLinje3', 5,  12, NULL),
(@lastIndik, 0, 'aktiv',                        'aktiv', 6,  16, NULL),
(@lastIndik, 0, 'ModifiedDate',                    NULL, 0,  93, NULL),
(@lastIndik, 0, 'ValidFrom',                'validFrom', 7,  93, NULL),
(@lastIndik, 0, 'ValidTo',                    'validTo', 8,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'indikationskode', 1, 'IndikationATCRef', NOW());
SET @lastIndikKode := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='indikationskode' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastIndikKode, 1, 'IndikationATCRefPID',              NULL, 0,  -5, NULL),
(@lastIndikKode, 0, 'CID',                              'id', 1,  12, NULL),
(@lastIndikKode, 0, 'IndikationKode',      'indikationskode', 2,  -5, NULL),
(@lastIndikKode, 0, 'ATC',                             'atc', 3,  12, NULL),
(@lastIndikKode, 0, 'DrugID',                       'drugId', 4,  -5, NULL),
(@lastIndikKode, 0, 'ModifiedDate',                     NULL, 0,  93, NULL),
(@lastIndikKode, 0, 'ValidFrom',                 'validFrom', 5,  93, NULL),
(@lastIndikKode, 0, 'ValidTo',                     'validTo', 6,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'klausulering', 1, 'Klausulering', NOW());
SET @lastKlaus := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='klausulering' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastKlaus, 1, 'KlausuleringPID',         NULL, 0,  -5, NULL),
(@lastKlaus, 0, 'Kode',                    'id', 1,  12, NULL),
(@lastKlaus, 0, 'KortTekst',        'kortTekst', 2,  12, NULL),
(@lastKlaus, 0, 'Tekst',                'tekst', 3,  12, NULL),
(@lastKlaus, 0, 'ModifiedDate',            NULL, 0,  93, NULL),
(@lastKlaus, 0, 'ValidFrom',        'validFrom', 4,  93, NULL),
(@lastKlaus, 0, 'ValidTo',            'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'laegemiddeladministrationsvejrelation', 1, 'LaegemiddelAdministrationsvejRef', NOW());
SET @lastLaegAdmVej := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='laegemiddeladministrationsvejrelation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastLaegAdmVej, 1, 'LaegemiddelAdministrationsvejRefPID',                  NULL, 0,  -5, NULL),
(@lastLaegAdmVej, 0, 'CID',                                                  'id', 1,  12, NULL),
(@lastLaegAdmVej, 0, 'DrugID',                                           'drugId', 2,  -5, NULL),
(@lastLaegAdmVej, 0, 'AdministrationsvejKode',             'administrationsvejId', 3,  12, NULL),
(@lastLaegAdmVej, 0, 'ModifiedDate',                                         NULL, 0,  93, NULL),
(@lastLaegAdmVej, 0, 'ValidFrom',                                     'validFrom', 4,  93, NULL),
(@lastLaegAdmVej, 0, 'ValidTo',                                         'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'laegemiddelnavn', 1, 'Laegemiddelnavn', NOW());
SET @lastLaegNavn := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='laegemiddelnavn' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastLaegNavn, 1, 'LaegemiddelnavnPID',                NULL, 0,  -5, NULL),
(@lastLaegNavn, 0, 'DrugID',                            'id', 1,  -5, NULL),
(@lastLaegNavn, 0, 'LaegemidletsUforkortedeNavn',     'navn', 2,  12, NULL),
(@lastLaegNavn, 0, 'ModifiedDate',                      NULL, 0,  93, NULL),
(@lastLaegNavn, 0, 'ValidFrom',                  'validFrom', 3,  93, NULL),
(@lastLaegNavn, 0, 'ValidTo',                      'validTo', 4,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'medicintilskud', 1, 'Medicintilskud', NOW());
SET @lastMedTil := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='medicintilskud' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastMedTil, 1, 'MedicintilskudPID',                NULL, 0,  -5, NULL),
(@lastMedTil, 0, 'Kode',                             'id', 1,  12, NULL),
(@lastMedTil, 0, 'KortTekst',                 'kortTekst', 2,  12, NULL),
(@lastMedTil, 0, 'Tekst',                         'tekst', 3,  12, NULL),
(@lastMedTil, 0, 'ModifiedDate',                     NULL, 0,  93, NULL),
(@lastMedTil, 0, 'ValidFrom',                 'validFrom', 4,  93, NULL),
(@lastMedTil, 0, 'ValidTo',                     'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'opbevaringsbetingelser', 1, 'Opbevaringsbetingelser', NOW());
SET @lastOpvb := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='opbevaringsbetingelser' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastOpvb, 1, 'OpbevaringsbetingelserPID',        NULL, 0,  -5, NULL),
(@lastOpvb, 0, 'Kode',                             'id', 1,  12, NULL),
(@lastOpvb, 0, 'KortTekst',                 'kortTekst', 2,  12, NULL),
(@lastOpvb, 0, 'Tekst',                         'tekst', 3,  12, NULL),
(@lastOpvb, 0, 'ModifiedDate',                     NULL, 0,  93, NULL),
(@lastOpvb, 0, 'ValidFrom',                 'validFrom', 4,  93, NULL),
(@lastOpvb, 0, 'ValidTo',                     'validTo', 5,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'dosisdispensering', 1, 'OplysningerOmDosisdispensering', NOW());
SET @lastDosDisp := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='dosisdispensering' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastDosDisp, 1, 'OplysningerOmDosisdispenseringPID',                NULL, 0,  -5, NULL),
(@lastDosDisp, 0, 'Varenummer',                                       'id', 1,  -5, NULL),
(@lastDosDisp, 0, 'DrugID',                                       'drugId', 2,  -5, NULL),
(@lastDosDisp, 0, 'LaegemidletsSubstitutionsgruppe', 'substitutionsgruppe', 3,  12, NULL),
(@lastDosDisp, 0, 'MindsteAIPPrEnhed',                 'mindsteAIPPrEnhed', 4,  -5, NULL),
(@lastDosDisp, 0, 'MindsteRegisterprisEnh',     'mindsteRegisterprisEnhed', 5,  -5, NULL),
(@lastDosDisp, 0, 'TSPPrEnhed',                               'tspPrEnhed', 6,  -5, NULL),
(@lastDosDisp, 0, 'BilligsteDrugid',                     'billigsteDrugid', 7,  -5, NULL),
(@lastDosDisp, 0, 'KodeForBilligsteDrugid',       'KodeForBilligsteDrugid', 8,  12, NULL),
(@lastDosDisp, 0, 'ModifiedDate',                                  NULL, 0,93, NULL),
(@lastDosDisp, 0, 'ValidFrom',                                 'validFrom', 9,  93, NULL),
(@lastDosDisp, 0, 'ValidTo',                                     'validTo',10,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'pakning', 1, 'Pakning', NOW());
SET @lastPakning := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='pakning' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPakning, 1, 'PakningPID',                                               NULL, 0,  -5, NULL),
(@lastPakning, 0, 'Varenummer',                                       'varenummer', 1,  -5, NULL),
(@lastPakning, 0, 'VarenummerDelpakning',                   'varenummerDelpakning', 2,  -5, NULL),
(@lastPakning, 0, 'DrugID',                                               'drugId', 3,  -5, NULL),
(@lastPakning, 0, 'PakningsstoerrelseNumerisk',       'pakningsstoerrelseNumerisk', 4,   3, NULL),
(@lastPakning, 0, 'Pakningsstoerrelsesenhed',            'pakningsstoerrelseEnhed', 5,  12, NULL),
(@lastPakning, 0, 'PakningsstoerrelseTekst',             'pakningsstoerrelseTekst', 6,  12, NULL),
(@lastPakning, 0, 'EmballageTypeKode',                         'emballageTypeKode', 7,  12, NULL),
(@lastPakning, 0, 'Dosisdispenserbar',                         'dosisdispenserbar', 8,  16, NULL),
(@lastPakning, 0, 'MedicintilskudsKode',                     'medicintilskudsKode', 9,  12, NULL),
(@lastPakning, 0, 'KlausuleringsKode',                         'klausuleringsKode',10,  12, NULL),
(@lastPakning, 0, 'AlfabetSekvensnr',                       'alfabetSekvensNummer',11,  -5, NULL),
(@lastPakning, 0, 'AntalDelpakninger',                         'antalDelpakninger',12,  -5, NULL),
(@lastPakning, 0, 'Udleveringsbestemmelse',               'udleveringsbestemmelse',13,  12, NULL),
(@lastPakning, 0, 'UdleveringSpeciale',                       'udleveringSpeciale',14,  12, NULL),
(@lastPakning, 0, 'AntalDDDPrPakning',                         'antalDDDPrPakning',15,   3, NULL),
(@lastPakning, 0, 'OpbevaringstidNumerisk',               'opbevaringstidNumerisk',16,  -5, NULL),
(@lastPakning, 0, 'Opbevaringstid',                               'opbevaringstid',17,  -5, NULL),
(@lastPakning, 0, 'Opbevaringsbetingelser',               'opbevaringsbetingelser',18,  12, NULL),
(@lastPakning, 0, 'Oprettelsesdato',                             'oprettelsesdato',19,  91, NULL),
(@lastPakning, 0, 'DatoForSenestePrisaendring',       'datoForSenestePrisaendring',20,  91, NULL),
(@lastPakning, 0, 'UdgaaetDato',                                     'udgaaetDato',21,  91, NULL),
(@lastPakning, 0, 'BeregningskodeAIRegpris',             'BeregningskodeAIRegpris',22,  12, NULL),
(@lastPakning, 0, 'PakningOptagetITilskudsgruppe', 'pakningOptagetITilskudsgruppe',23,  16, NULL),
(@lastPakning, 0, 'Faerdigfremstillingsgebyr',         'faerdigfremstillingsgebyr',24,  16, NULL),
(@lastPakning, 0, 'Pakningsdistributoer',                   'pakningsdistributoer',25,  -5, NULL),
(@lastPakning, 0, 'ModifiedDate',                     NULL, 0,  93, NULL),
(@lastPakning, 0, 'ValidFrom',                 'validFrom',26,  93, NULL),
(@lastPakning, 0, 'ValidTo',                     'validTo',27,  93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'pakningskombination', 1, 'Pakningskombinationer', NOW());
SET @lastPakKom := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='pakningskombination' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPakKom, 1, 'PakningskombinationerPID',                                  NULL, 0,  -5, NULL),
(@lastPakKom, 0, 'CID',                                                       'id', 1,  12, NULL),
(@lastPakKom, 0, 'VarenummerOrdineret',                      'varenummerOrdineret', 2,  -5, NULL),
(@lastPakKom, 0, 'VarenummerSubstitueret',                'varenummerSubstitueret', 3,  -5, NULL),
(@lastPakKom, 0, 'VarenummerAlternativt',                  'varenummerAlternativt', 4,  -5, NULL),
(@lastPakKom, 0, 'AntalPakninger',                                'antalPakninger', 5,  -5, NULL),
(@lastPakKom, 0, 'EkspeditionensSamledePris',          'ekspeditionensSamledePris', 6,  -5, NULL),
(@lastPakKom, 0, 'InformationspligtMarkering',        'informationspligtMarkering', 7,  12, NULL),
(@lastPakKom, 0, 'ModifiedDate',                                     NULL, 0, 93, NULL),
(@lastPakKom, 0, 'ValidFrom',                                 'validFrom', 9, 93, NULL),
(@lastPakKom, 0, 'ValidTo',                                     'validTo',10, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'pakningskombinationudenpriser', 1, 'PakningskombinationerUdenPriser', NOW());
SET @lastPakKomUP := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='pakningskombinationudenpriser' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPakKomUP, 1, 'PakningskombinationerUdenPriserPID',                        NULL, 0,  -5, NULL),
(@lastPakKomUP, 0, 'VarenummerOrdineret',                    'varenummerOrdineret', 1,  -5, NULL),
(@lastPakKomUP, 0, 'VarenummerSubstitueret',              'varenummerSubstitueret', 2,  -5, NULL),
(@lastPakKomUP, 0, 'VarenummerAlternativt',                'varenummerAlternativt', 3,  -5, NULL),
(@lastPakKomUP, 0, 'AntalPakninger',                              'antalPakninger', 4,  -5, NULL),
(@lastPakKomUP, 0, 'InformationspligtMarkering',      'informationspligtMarkering', 5,  12, NULL),
(@lastPakKomUP, 0, 'ModifiedDate',                                     NULL, 0, 93, NULL),
(@lastPakKomUP, 0, 'ValidFrom',                                 'validFrom', 6, 93, NULL),
(@lastPakKomUP, 0, 'ValidTo',                                     'validTo', 7, 93, NULL);


INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'pakningsstoerrelsesenhed', 1, 'Pakningsstoerrelsesenhed', NOW());
SET @lastPakStoE := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='pakningsstoerrelsesenhed' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPakStoE, 1, 'PakningsstoerrelsesenhedPID',                 NULL, 0, -5, NULL),
(@lastPakStoE, 0, 'PakningsstoerrelsesenhedKode',                'id', 1, 12, NULL),
(@lastPakStoE, 0, 'PakningsstoerrelsesenhedTekst',            'tekst', 2, 12, NULL),
(@lastPakStoE, 0, 'PakningsstoerrelsesenhedKortTekst',    'kortTekst', 3, 12, NULL),
(@lastPakStoE, 0, 'ModifiedDate',                                NULL, 0, 93, NULL),
(@lastPakStoE, 0, 'ValidFrom',                            'validFrom', 4, 93, NULL),
(@lastPakStoE, 0, 'ValidTo',                                'validTo', 5, 93, NULL);


INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'pris', 1, 'Priser', NOW());
SET @lastPriser := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='pris' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastPriser, 1, 'PriserPID',                                         NULL, 0, -5, NULL),
(@lastPriser, 0, 'Varenummer',                                'varenummer', 1, -5, NULL),
(@lastPriser, 0, 'apoteketsIndkoebspris',          'apoteketsIndkoebspris', 2, -5, NULL),
(@lastPriser, 0, 'Registerpris',                            'registerpris', 3, -5, NULL),
(@lastPriser, 0, 'ekspeditionensSamledePris',  'ekspeditionensSamledePris', 3, -5, NULL),
(@lastPriser, 0, 'tilskudspris',                            'tilskudspris', 3, -5, NULL),
(@lastPriser, 0, 'LeveranceprisTilHospitaler','leveranceprisTilHospitaler', 3, -5, NULL),
(@lastPriser, 0, 'IkkeTilskudsberettigetDel',  'ikkeTilskudsberettigetDel', 3, -5, NULL),
(@lastPriser, 0, 'ModifiedDate',                                      NULL, 0, 93, NULL),
(@lastPriser, 0, 'ValidFrom',                                  'validFrom', 4, 93, NULL),
(@lastPriser, 0, 'ValidTo',                                      'validTo', 5, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'rekommandation', 1, 'Rekommandationer', NOW());
SET @lastRekom := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='rekommandation' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastRekom, 1, 'RekommandationerPID',           NULL, 0, -5, NULL),
(@lastRekom, 0, 'Varenummer',            'varenummer', 1, -5, NULL),
(@lastRekom, 0, 'Rekommandationsgruppe',     'gruppe', 2, -5, NULL),
(@lastRekom, 0, 'DrugID',                    'drugId', 3, -5, NULL),
(@lastRekom, 0, 'Rekommandationsniveau',     'niveau', 4, 12, NULL),
(@lastRekom, 0, 'ModifiedDate',                  NULL, 0, 93, NULL),
(@lastRekom, 0, 'ValidFrom',              'validFrom', 5, 93, NULL),
(@lastRekom, 0, 'ValidTo',                  'validTo', 6, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'specialefornbs', 1, 'SpecialeForNBS', NOW());
SET @lastSFNBS := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='specialefornbs' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSFNBS, 1, 'SpecialeForNBSPID',        NULL, 0, -5, NULL),
(@lastSFNBS, 0, 'Kode',                     'id', 1, 12, NULL),
(@lastSFNBS, 0, 'KortTekst',         'kortTekst', 2, 12, NULL),
(@lastSFNBS, 0, 'Tekst',                 'tekst', 3, 12, NULL),
(@lastSFNBS, 0, 'ModifiedDate',             NULL, 0, 93, NULL),
(@lastSFNBS, 0, 'ValidFrom',         'validFrom', 5, 93, NULL),
(@lastSFNBS, 0, 'ValidTo',             'validTo', 6, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'styrkeenhed', 1, 'Styrkeenhed', NOW());
SET @lastSFNBS := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='styrkeenhed' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSFNBS, 1, 'StyrkeenhedPID',               NULL, 0, -5, NULL),
(@lastSFNBS, 0, 'StyrkeenhedKode',              'id', 1, 12, NULL),
(@lastSFNBS, 0, 'StyrkeenhedTekst',          'tekst', 2, 12, NULL),
(@lastSFNBS, 0, 'StyrkeenhedKortTekst',  'kortTekst', 3, 12, NULL),
(@lastSFNBS, 0, 'ModifiedDate',                 NULL, 0, 93, NULL),
(@lastSFNBS, 0, 'ValidFrom',             'validFrom', 4, 93, NULL),
(@lastSFNBS, 0, 'ValidTo',                 'validTo', 5, 93, NULL);


INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'substitution', 1, 'Substitution', NOW());
SET @lastSubst := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='substitution' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSubst, 1, 'SubstitutionPID',                                       NULL, 0, -5, NULL),
(@lastSubst, 0, 'ReceptensVarenummer',                     'receptvarenummer', 1, -5, NULL),
(@lastSubst, 0, 'Substitutionsgruppenummer',      'substitutionsgruppenummer', 2, -5, NULL),
(@lastSubst, 0, 'NumeriskPakningsstoerrelse',    'PakningsstoerrelseNumerisk', 3, -5, NULL),
(@lastSubst, 0, 'ProdAlfabetiskeSekvensplads',  'ProdAlfabetiskeSekvensplads', 4, 12, NULL),
(@lastSubst, 0, 'SubstitutionskodeForPakning',  'SubstitutionskodeForPakning', 5, 12, NULL),
(@lastSubst, 0, 'BilligsteVarenummer',                  'billigsteVarenummer', 6, -5, NULL),
(@lastSubst, 0, 'ModifiedDate',                 NULL, 0, 93, NULL),
(@lastSubst, 0, 'ValidFrom',             'validFrom', 7, 93, NULL),
(@lastSubst, 0, 'ValidTo',                 'validTo', 8, 93, NULL);


INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'substitutionaflaegemidlerudenfastpris', 1, 'SubstitutionAfLaegemidlerUdenFastPris', NOW());
SET @lastSubstAfLUF := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='substitutionaflaegemidlerudenfastpris' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastSubstAfLUF, 1, 'SubstitutionAfLaegemidlerUdenFastPrisPID',                       NULL, 0, -5, NULL),
(@lastSubstAfLUF, 0, 'Varenummer',                                             'varenummer', 1, -5, NULL),
(@lastSubstAfLUF, 0, 'Substitutionsgruppenummer',               'substitutionsgruppenummer', 2, -5, NULL),
(@lastSubstAfLUF, 0, 'ModifiedDate',                 NULL, 0, 93, NULL),
(@lastSubstAfLUF, 0, 'ValidFrom',             'validFrom', 3, 93, NULL),
(@lastSubstAfLUF, 0, 'ValidTo',                 'validTo', 4, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'takstversion', 1, 'TakstVersion', NOW());
SET @lastTakstV := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='takstversion' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastTakstV, 1, 'TakstVersionPID',              NULL, 0, -5, NULL),
(@lastTakstV, 0, 'TakstUge',               'takstUge', 1, 12, NULL),
(@lastTakstV, 0, 'ModifiedDate',                 NULL, 0, 93, NULL),
(@lastTakstV, 0, 'ValidFrom',             'validFrom', 2, 93, NULL),
(@lastTakstV, 0, 'ValidTo',                 'validTo', 3, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'tidsenhed', 1, 'Tidsenhed', NOW());
SET @lastTidsenhed := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='tidsenhed' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastTidsenhed, 1, 'TidsenhedPID',                 NULL, 0, -5, NULL),
(@lastTidsenhed, 0, 'TidsenhedKode',                'id', 1, 12, NULL),
(@lastTidsenhed, 0, 'TidsenhedTekst',            'tekst', 2, 12, NULL),
(@lastTidsenhed, 0, 'TidsenhedKortTekst',    'kortTekst', 3, 12, NULL),
(@lastTidsenhed, 0, 'ModifiedDate',                 NULL, 0, 93, NULL),
(@lastTidsenhed, 0, 'ValidFrom',             'validFrom', 4, 93, NULL),
(@lastTidsenhed, 0, 'ValidTo',                 'validTo', 5, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'tilskudsinterval', 1, 'Tilskudsintervaller', NOW());
SET @lastTilskInt := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='tilskudsinterval' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastTilskInt, 1, 'TilskudsintervallerPID',    NULL, 0, -5, NULL),
(@lastTilskInt, 0, 'CID',                       'id', 1, 12, NULL),
(@lastTilskInt, 0, 'Type',                    'type', 2,  4, NULL),
(@lastTilskInt, 0, 'Niveau',                'niveau', 3,  4, NULL),
(@lastTilskInt, 0, 'NedreGraense',    'nedreGraense', 4, -5, NULL),
(@lastTilskInt, 0, 'OevreGraense',    'OevreGraense', 5, -5, NULL),
(@lastTilskInt, 0, 'Procent',              'procent', 6,  3, NULL),
(@lastTilskInt, 0, 'ModifiedDate',              NULL, 0, 93, NULL),
(@lastTilskInt, 0, 'ValidFrom',          'validFrom', 7, 93, NULL),
(@lastTilskInt, 0, 'ValidTo',              'validTo', 8, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'tilskudsprisgrupperpakningsniveau', 1, 'TilskudsprisgrupperPakningsniveau', NOW());
SET @lastTilskGrPakN := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='tilskudsprisgrupperpakningsniveau' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastTilskGrPakN, 1, 'TilskudsprisgrupperPakningsniveauPID',                 NULL, 0, -5, NULL),
(@lastTilskGrPakN, 0, 'Varenummer',                                   'varenummer', 1, -5, NULL),
(@lastTilskGrPakN, 0, 'TilskudsprisGruppe',                   'TilskudsprisGruppe', 2, -5, NULL),
(@lastTilskGrPakN, 0, 'ModifiedDate',              NULL, 0, 93, NULL),
(@lastTilskGrPakN, 0, 'ValidFrom',          'validFrom', 3, 93, NULL),
(@lastTilskGrPakN, 0, 'ValidTo',              'validTo', 4, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'udgaaedenavne', 1, 'UdgaaedeNavne', NOW());
SET @lastUdgaaNa := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='udgaaedenavne' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastUdgaaNa, 1, 'UdgaaedeNavnePID',                 NULL, 0, -5, NULL),
(@lastUdgaaNa, 0, 'CID',                              'id', 1, 12, NULL),
(@lastUdgaaNa, 0, 'Drugid',                       'drugId', 2, -5, NULL),
(@lastUdgaaNa, 0, 'DatoForAendringen', 'datoForAendringen', 3, 91, NULL),
(@lastUdgaaNa, 0, 'TidligereNavn',         'tidligereNavn', 4, 12, NULL),
(@lastUdgaaNa, 0, 'ModifiedDate',                     NULL, 0, 93, NULL),
(@lastUdgaaNa, 0, 'ValidFrom',                 'validFrom', 5, 93, NULL),
(@lastUdgaaNa, 0, 'ValidTo',                     'validTo', 6, 93, NULL);

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('dkma', 'udleveringsbestemmelse', 1, 'Udleveringsbestemmelser', NOW());
SET @lastUdlevB := (SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='dkma' AND datatype='udleveringsbestemmelse' AND version=1);
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
(@lastUdlevB, 1, 'UdleveringsbestemmelserPID',       NULL, 0, -5, NULL),
(@lastUdlevB, 0, 'Kode',                             'id', 1, 12, NULL),
(@lastUdlevB, 0, 'Udleveringsgruppe', 'udleveringsgruppe', 2, 12, NULL),
(@lastUdlevB, 0, 'KortTekst',                 'kortTekst', 3, 12, NULL),
(@lastUdlevB, 0, 'Tekst',                         'tekst', 4, 12, NULL),
(@lastUdlevB, 0, 'ModifiedDate',                     NULL, 0, 93, NULL),
(@lastUdlevB, 0, 'ValidFrom',                 'validFrom', 5, 93, NULL),
(@lastUdlevB, 0, 'ValidTo',                     'validTo', 6, 93, NULL);

