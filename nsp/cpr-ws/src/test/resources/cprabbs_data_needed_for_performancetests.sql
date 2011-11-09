USE nsp_stamdata;

TRUNCATE TABLE ChangesToCPR;

LOCK TABLES `ChangesToCPR` WRITE;

INSERT INTO `ChangesToCPR` (`CPR`, `ModifiedDate`)
VALUES
	('1212121234','2010-10-06 14:48:50');

UNLOCK TABLES;

use nsp_reg_noti;

TRUNCATE TABLE cprsubscription;

LOCK TABLES `cprsubscription` WRITE;

INSERT INTO `cprsubscription` (`pk`, `cvr`, `cpr`)
VALUES
	(1,'22334455','1212121234');

UNLOCK TABLES;