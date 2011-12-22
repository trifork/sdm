use nsp_reg_noti;

TRUNCATE TABLE CprSubscription;

LOCK TABLES `CprSubscription` WRITE;

INSERT INTO `CprSubscription` (`pk`, `cvr`, `cpr`)
VALUES
	(1,'22334455','1212121234');

UNLOCK TABLES;