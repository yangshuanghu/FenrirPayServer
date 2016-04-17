CREATE DATABASE goods_db
CHARACTER SET 'utf8'
COLLATE 'utf8_general_ci';
CREATE TABLE `goods_db`.`user_table` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` TEXT NOT NULL,
  `password` TEXT NOT NULL,
  `username` TEXT NULL,
  `staff_id` INT NOT NULL,
  `token` TEXT NOT NULL,
  `point` FLOAT NULL,
  `money` FLOAT NULL,
  `permission` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8;
CREATE TABLE `goods_db`.`history_table` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `pay_time` DATETIME NOT NULL,
  `goods_bar_code` TEXT NOT NULL,
  `pay_count` INT NOT NULL,
  `staff_id` INT NOT NULL,
  `spend` FLOAT NOT NULL,
  `token` TEXT NOT NULL,
  `show_flag` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8;
CREATE TABLE `goods_db`.`goods_table` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` TEXT NOT NULL,
  `bar_code` TEXT NOT NULL,
  `sale_price` FLOAT NOT NULL,
  `cost_price` FLOAT NOT NULL,
  `amount` INT NOT NULL,
  `unit` TEXT NOT NULL,
  `package_num` INT NOT NULL,
  `note` TEXT NULL,
  `class_name` TEXT NULL,
  `create_date` DATETIME NOT NULL,
  `update_date` DATETIME NULL,
  PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8;
CREATE TABLE `goods_db`.`charge_history_table` (
  `id` INT NOT NULL,
  `staff_id` INT NOT NULL,
  `charge` FLOAT NOT NULL,
  `charge_date` DATETIME NOT NULL,
  `token` TEXT NOT NULL,
  `show_flag` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8;