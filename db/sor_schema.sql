SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `SorStatus`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `SorStatus` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `fromDate` DATE NOT NULL ,
  `toDate` DATE NULL DEFAULT NULL ,
  `updatedAt` DATE NULL DEFAULT NULL ,
  `firstFromDate` DATE NOT NULL ,
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `EanLocationCode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `EanLocationCode` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `eanLocationCode` BIGINT NOT NULL ,
  `onlyInternalIndicator` CHAR NOT NULL ,
  `nonActiveIndicator` CHAR NOT NULL ,
  `systemSupplier` BIGINT NOT NULL ,
  `systemType` BIGINT NOT NULL ,
  `communicationSupplier` BIGINT NOT NULL ,
  `regionCode` BIGINT NOT NULL ,
  `ediAdministrator` BIGINT NOT NULL ,
  `ediAdministrator` BIGINT NOT NULL ,
  `sorNote` VARCHAR(254) NULL DEFAULT NULL ,
  `sorStatusId` BIGINT NULL DEFAULT NULL ,
  PRIMARY KEY (`pk`) ,
  INDEX `fk_sorStatus` (`sorStatusId` ASC) ,
  CONSTRAINT `fk_sorStatus`
    FOREIGN KEY (`sorStatusId` )
    REFERENCES `SorStatus` (`pk` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `CountryIdentificationCodeType`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `CountryIdentificationCodeType` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `scheme` ENUM('iso3166-alpha2','iso3166-alpha3','un-numeric3','imk') NOT NULL ,
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PostalAddressInformation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `PostalAddressInformation` (
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
  `countryIdentificationCodeTypeId` BIGINT NULL ,
  `countryIdentificationCode` VARCHAR(10) NULL ,
  `stairway` VARCHAR(40) NULL ,
  PRIMARY KEY (`pk`) ,
  INDEX `fk_countryIdentitifationCodeTypeScheme` (`countryIdentificationCodeTypeId` ASC) ,
  CONSTRAINT `fk_countryIdentitifationCodeTypeScheme`
    FOREIGN KEY (`countryIdentificationCodeTypeId` )
    REFERENCES `CountryIdentificationCodeType` (`pk` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `VirtualAddressInformation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `VirtualAddressInformation` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `emailAddressIdentifier` VARCHAR(254) NULL ,
  `website` VARCHAR(254) NULL ,
  `telephoneNumberIdentifier` VARCHAR(20) NOT NULL ,
  `faxNumberIdentifier` VARCHAR(20) NULL ,
  PRIMARY KEY (`pk`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `InstitutionOwner`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `InstitutionOwner` (
  `pk` BIGINT NOT NULL AUTO_INCREMENT ,
  `sorIdentifier` BIGINT NOT NULL ,
  `entityName` VARCHAR(60) NOT NULL ,
  `ownerType` BIGINT NOT NULL ,
  `eanLocationCodeId` BIGINT NULL DEFAULT NULL ,
  `postalAddressInformationId` BIGINT NOT NULL ,
  `virtualAddressInformationId` BIGINT NOT NULL ,
  `sorStatusId` BIGINT NOT NULL ,
  PRIMARY KEY (`pk`) ,
  INDEX `fk_eanLocationCode` (`eanLocationCodeId` ASC) ,
  INDEX `fk_postalAddressInformation` (`postalAddressInformationId` ASC) ,
  INDEX `fk_virtualAddressInformation` (`virtualAddressInformationId` ASC) ,
  INDEX `fk_sorStatus` (`sorStatusId` ASC) ,
  CONSTRAINT `fk_eanLocationCode`
    FOREIGN KEY (`eanLocationCodeId` )
    REFERENCES `EanLocationCode` (`pk` ),
  CONSTRAINT `fk_postalAddressInformation`
    FOREIGN KEY (`postalAddressInformationId` )
    REFERENCES `PostalAddressInformation` (`pk` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_virtualAddressInformation`
    FOREIGN KEY (`virtualAddressInformationId` )
    REFERENCES `VirtualAddressInformation` (`pk` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sorStatus`
    FOREIGN KEY (`sorStatusId` )
    REFERENCES `SorStatus` (`pk` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
