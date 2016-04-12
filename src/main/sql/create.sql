CREATE TABLE `goods_db`.`user_table` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` TEXT NOT NULL,
  `password` TEXT NOT NULL,
  `username` TEXT NULL,
  `staff_id` INT NOT NULL,
  `token` TEXT NOT NULL,
  PRIMARY KEY (`id`));
CREATE TABLE `goods_db`.`history_table` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `time` DATETIME NOT NULL,
  `goods_bar_code` TEXT NOT NULL,
  `user_token` TEXT NOT NULL,
  `spend` FLOAT NOT NULL,
  PRIMARY KEY (`id`));
CREATE TABLE `goods_db`.`goods_table` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` TEXT NOT NULL,
  `bar_code` TEXT NOT NULL,
  `sale_price` FLOAT NOT NULL,
  `cost_price` FLOAT NOT NULL,
  `count` FLOAT NOT NULL,
  `unit` TEXT NOT NULL,
  `package_num` FLOAT NOT NULL,
  `note` TEXT NULL,
  `class` TEXT NULL,
  `create_date` DATETIME NOT NULL,
  `update_date` DATETIME NULL,
  PRIMARY KEY (`id`));