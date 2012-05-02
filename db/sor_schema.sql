SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `sdm_warehouse` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `sdm_warehouse` ;

-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORInstitutionOwner`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORInstitutionOwner` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORInstitutionOwner` (
  `PID` BIGINT NOT NULL AUTO_INCREMENT ,
  `sorIdentifier` BIGINT NOT NULL ,
  `entityName` VARCHAR(60) NOT NULL ,
  `ownerType` BIGINT NOT NULL ,
  `fkEanLocationCode` BIGINT NULL DEFAULT NULL ,
  `fkPostalAddressInformation` BIGINT NOT NULL ,
  `fkVirtualAddressInfomation` BIGINT NOT NULL ,
  `fkSorStatus` BIGINT NOT NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SOREanLocationCode`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SOREanLocationCode` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SOREanLocationCode` (
  `PID` BIGINT NOT NULL AUTO_INCREMENT ,
  `eanLocationCode` BIGINT NOT NULL ,
  `onlyInternalIndicator` CHAR NOT NULL ,
  `nonActiveIndicator` CHAR NOT NULL ,
  `systemSupplier` BIGINT NOT NULL ,
  `systemType` BIGINT NOT NULL ,
  `communicationSupplier` BIGINT NOT NULL ,
  `regionCode` BIGINT NOT NULL ,
  `ediAdministrator` BIGINT NOT NULL ,
  `sorNote` VARCHAR(254) NULL DEFAULT NULL ,
  `fkSorStatus` BIGINT NULL DEFAULT NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORSorStatus`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORSorStatus` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORSorStatus` (
  `PID` BIGINT NOT NULL AUTO_INCREMENT ,
  `fromDate` VARCHAR(10) NOT NULL ,
  `toDate` VARCHAR(10) NULL DEFAULT NULL ,
  `updatedAt` VARCHAR(10) NULL DEFAULT NULL ,
  `firstFromDate` VARCHAR(10) NOT NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORPostalAddressInformation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORPostalAddressInformation` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORPostalAddressInformation` (
  `PID` BIGINT NOT NULL AUTO_INCREMENT ,
  `mailDeliverySublocationIdentifier` VARCHAR(34) NULL ,
  `streetName` VARCHAR(40) NOT NULL ,
  `streetNameForAddressingName` VARCHAR(20) NULL ,
  `streetBuildingIdentifier` VARCHAR(10) NOT NULL ,
  `floorIdentifier` VARCHAR(10) NULL ,
  `suiteIdentifier` VARCHAR(4) NULL ,
  `districtSubdivisionIdentifier` VARCHAR(34) NULL ,
  `postOfficeBoxIdentifier` INT NULL ,
  `postCodeIdentifier` INT NULL ,
  `districtName` VARCHAR(20) NULL ,
  `countryIdentificationCodeScheme` SMALLINT NULL ,
  `countryIdentificationCode` VARCHAR(10) NULL ,
  `stairway` VARCHAR(40) NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORVirtualAddressInformation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORVirtualAddressInformation` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORVirtualAddressInformation` (
  `PID` BIGINT NOT NULL AUTO_INCREMENT ,
  `emailAddressIdentifier` VARCHAR(254) NULL ,
  `website` VARCHAR(254) NULL ,
  `telephoneNumberIdentifier` VARCHAR(20) NOT NULL ,
  `faxNumberIdentifier` VARCHAR(20) NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SOROrganizationalUnit`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SOROrganizationalUnit` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SOROrganizationalUnit` (
  `PID` BIGINT NOT NULL AUTO_INCREMENT ,
  `sorIdentifier` BIGINT NOT NULL ,
  `entityName` VARCHAR(60) NOT NULL ,
  `fkOrganizationalChildUnit` BIGINT NULL ,
  `unitType` BIGINT NULL ,
  `localCode` VARCHAR(20) NULL ,
  `pharmacyIdentifier` VARCHAR(20) NULL ,
  `shakIdentifier` VARCHAR(7) NULL ,
  `providerIdentifier` VARCHAR(9) NULL ,
  `eanLocationCodeInheritedIndicator` CHAR(1) NULL ,
  `fkEanLocationCode` BIGINT NULL ,
  `geographicalParentRelation` BIGINT NOT NULL ,
  `geographicalParentSorIdentifier` BIGINT NULL ,
  `fkPostalAddressInformation` BIGINT NULL ,
  `fkVisitingAddressInformation` BIGINT NULL ,
  `fkActivityAddressInformation` BIGINT NULL ,
  `fkVirtualAddressInfomation` BIGINT NULL ,
  `fkClinicalSpecialityColleaction` BIGINT NULL ,
  `fkSorStatus` BIGINT NOT NULL ,
  `fkReplacesSorCollection` BIGINT NULL ,
  `fkReplacedByCollection` BIGINT NULL ,
  `ambulantActivityIndicator` VARCHAR(1) NULL ,
  `patientsAdmittedIndicator` VARCHAR(1) NULL ,
  `reportingLevelIndicator` VARCHAR(1) NULL ,
  `localAttribute1` VARCHAR(20) NULL ,
  `localAttribute2` VARCHAR(20) NULL ,
  `localAttribute3` VARCHAR(20) NULL ,
  `localAttribute4` VARCHAR(20) NULL ,
  `localAttribute5` VARCHAR(20) NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORHealthInstitution`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORHealthInstitution` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORHealthInstitution` (
  `PID` BIGINT NOT NULL AUTO_INCREMENT ,
  `sorIdentifier` BIGINT NOT NULL ,
  `entityName` VARCHAR(60) NOT NULL ,
  `institutionType` BIGINT NOT NULL ,
  `pharmacyIdentifier` VARCHAR(20) NULL ,
  `shakIdentifier` VARCHAR(7) NULL ,
  `eanLocationCodeInheritedIndicator` VARCHAR(1) NULL ,
  `fkEanLocationCode` BIGINT NULL ,
  `geographicalParentRelation` BIGINT NULL ,
  `geographicalParentSorIdentifier` BIGINT NULL ,
  `fkPostalAddressInformation` BIGINT NULL ,
  `fkVisitingAddressInformation` BIGINT NULL ,
  `fkActivityAddressInformation` BIGINT NULL ,
  `fkVirtualAddressInfomation` BIGINT NULL ,
  `fkSorStatus` BIGINT NOT NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`PID`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
