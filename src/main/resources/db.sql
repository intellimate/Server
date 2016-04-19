-- MySQL Script generated by MySQL Workbench
-- Tue Apr 19 23:41:25 2016
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

-- -----------------------------------------------------
-- Schema izou_server
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema izou_server
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `izou_server` DEFAULT CHARACTER SET utf8mb4 ;
USE `izou_server` ;

-- -----------------------------------------------------
-- Table `izou_server`.`User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`User` (
  `id_user` INT NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `root` BIT(1) NOT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`App`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`App` (
  `id_app` INT NOT NULL,
  `developer` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id_app`),
  INDEX `developer_idx` (`developer` ASC),
  CONSTRAINT `app_developer`
    FOREIGN KEY (`developer`)
    REFERENCES `izou_server`.`User` (`id_user`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`App_Version`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`App_Version` (
  `id_App_Version` INT NOT NULL,
  `app` INT NOT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `major` INT NOT NULL,
  `minor` INT NOT NULL,
  `patch` INT NOT NULL,
  PRIMARY KEY (`id_App_Version`),
  INDEX `versioned_app_idx` (`app` ASC),
  CONSTRAINT `versioned_app`
    FOREIGN KEY (`app`)
    REFERENCES `izou_server`.`App` (`id_app`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`App_Instance`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`App_Instance` (
  `id_App_Instance` INT NOT NULL,
  `app_reference` INT NOT NULL,
  `platform` VARCHAR(255) NOT NULL,
  `active` BIT(1) NOT NULL,
  PRIMARY KEY (`id_App_Instance`),
  INDEX `app_instance_ref_idx` (`app_reference` ASC),
  CONSTRAINT `app_instance_ref`
    FOREIGN KEY (`app_reference`)
    REFERENCES `izou_server`.`App_Version` (`id_App_Version`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`Izou_Instance`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`Izou_Instance` (
  `id_Instances` INT NOT NULL,
  `user` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_Instances`),
  INDEX `izou_instance_user_idx` (`user` ASC),
  CONSTRAINT `izou_instance_user`
    FOREIGN KEY (`user`)
    REFERENCES `izou_server`.`User` (`id_user`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`App_Tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`App_Tag` (
  `id_App_Tags` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_App_Tags`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`App_Active_Tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`App_Active_Tag` (
  `id_App_Active_Tag` INT NOT NULL,
  `app` INT NOT NULL,
  `tag` INT NOT NULL,
  PRIMARY KEY (`id_App_Active_Tag`),
  INDEX `app_active_tag_app_ref_idx` (`app` ASC),
  INDEX `app_active_tag_tag_ref_idx` (`tag` ASC),
  CONSTRAINT `app_active_tag_app_ref`
    FOREIGN KEY (`app`)
    REFERENCES `izou_server`.`App` (`id_app`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `app_active_tag_tag_ref`
    FOREIGN KEY (`tag`)
    REFERENCES `izou_server`.`App_Tag` (`id_App_Tags`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`App_Dependency`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`App_Dependency` (
  `id_App_Dependency` INT NOT NULL,
  `subject` INT NOT NULL,
  `dependency` INT NOT NULL,
  PRIMARY KEY (`id_App_Dependency`),
  INDEX `app_dependency_subject_idx` (`subject` ASC),
  INDEX `app_dependency_dependency_idx` (`dependency` ASC),
  CONSTRAINT `app_dependency_subject`
    FOREIGN KEY (`subject`)
    REFERENCES `izou_server`.`App` (`id_app`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `app_dependency_dependency`
    FOREIGN KEY (`dependency`)
    REFERENCES `izou_server`.`App` (`id_app`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`Database_Version`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`Database_Version` (
  `id_Database_Version` INT NOT NULL,
  `version` INT NOT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_Database_Version`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `izou_server`.`Izou`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `izou_server`.`Izou` (
  `id_Izou` INT NOT NULL,
  `active` BIT(1) NOT NULL,
  `major` INT NOT NULL,
  `minor` INT NOT NULL,
  `patch` INT NOT NULL,
  `version` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_Izou`),
  UNIQUE INDEX `version_UNIQUE` (`version` ASC))
ENGINE = InnoDB;
