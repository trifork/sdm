SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `sdm_warehouse` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `sdm_warehouse` ;

-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SOREanLocationCode`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SOREanLocationCode` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SOREanLocationCode` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `eanLocationCode` BIGINT NOT NULL ,
  `onlyInternalIndicator` CHAR NOT NULL ,
  `nonActiveIndicator` CHAR NOT NULL ,
  `systemSupplier` BIGINT NOT NULL ,
  `systemType` BIGINT NOT NULL ,
  `communicationSupplier` BIGINT NOT NULL ,
  `regionCode` BIGINT NOT NULL ,
  `ediAdministrator` BIGINT NOT NULL ,
  `sorNote` VARCHAR(254) NULL DEFAULT NULL ,
  `sorStatusId` BIGINT NULL DEFAULT NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORPostalAddressInformation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORPostalAddressInformation` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORPostalAddressInformation` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
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
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORVirtualAddressInformation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORVirtualAddressInformation` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORVirtualAddressInformation` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `emailAddressIdentifier` VARCHAR(254) NULL ,
  `website` VARCHAR(254) NULL ,
  `telephoneNumberIdentifier` VARCHAR(20) NOT NULL ,
  `faxNumberIdentifier` VARCHAR(20) NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORInstitutionOwner`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORInstitutionOwner` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORInstitutionOwner` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `sorIdentifier` BIGINT NOT NULL ,
  `entityName` VARCHAR(60) NOT NULL ,
  `ownerType` BIGINT NOT NULL ,
  `eanLocationCodeId` BIGINT NULL DEFAULT NULL ,
  `postalAddressInformationId` BIGINT NULL ,
  `virtualAddressInformationId` BIGINT NULL ,
  `sorStatusId` BIGINT NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedDate` DATETIME NOT NULL ,
  PRIMARY KEY (`pk`) ,
  INDEX `fk_eanLocationCode` (`eanLocationCodeId` ASC) ,
  INDEX `fk_postalAddressInformation` (`postalAddressInformationId` ASC) ,
  INDEX `fk_virtualAddressInformation` (`virtualAddressInformationId` ASC) ,
  CONSTRAINT `fk_eanLocationCode`
    FOREIGN KEY (`eanLocationCodeId` )
    REFERENCES `sdm_warehouse`.`SOREanLocationCode` (`pk` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_postalAddressInformation`
    FOREIGN KEY (`postalAddressInformationId` )
    REFERENCES `sdm_warehouse`.`SORPostalAddressInformation` (`pk` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_virtualAddressInformation`
    FOREIGN KEY (`virtualAddressInformationId` )
    REFERENCES `sdm_warehouse`.`SORVirtualAddressInformation` (`pk` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SORSorStatus`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SORSorStatus` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SORSorStatus` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `fromDate` DATE NOT NULL ,
  `toDate` DATE NULL DEFAULT NULL ,
  `updatedAt` DATE NULL DEFAULT NULL ,
  `firstFromDate` DATE NOT NULL ,
  `ValidFrom` DATETIME NOT NULL ,
  `ValidTo` DATETIME NULL ,
  `ModifiedTime` DATETIME NULL ,
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `sdm_warehouse`.`SOROrganizationalUnit`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sdm_warehouse`.`SOROrganizationalUnit` ;

CREATE  TABLE IF NOT EXISTS `sdm_warehouse`.`SOROrganizationalUnit` (
  `pk` BIGINT NOT NULL ,
  `sorIdentifier` BIGINT NOT NULL ,
  `entityName` VARCHAR(60) NOT NULL ,
  `unitType` BIGINT NULL ,
  `locationCode` VARCHAR(20) NULL ,
  `pharmacyIdentifier` VARCHAR(20) NULL ,
  `shakIdentifier` VARCHAR(7) NULL ,
  `providerIdentifier` VARCHAR(9) NULL ,
  `fkOptionalEanLocationCode` BIGINT NOT NULL ,
  `fkGeographicalParent` BIGINT NOT NULL ,
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
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
