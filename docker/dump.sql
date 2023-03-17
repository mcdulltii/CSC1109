CREATE TABLE `transactions` (
  `transactionId` varchar(8) NOT NULL,
  `accountNumber` varchar(50) NOT NULL,
  `transactionDate` date NOT NULL,
  `transactionDetails` varchar(100) DEFAULT NULL,
  `chqNumber` varchar(50) DEFAULT NULL,
  `valueDate` date NOT NULL,
  `withdrawal` decimal(15,2) DEFAULT NULL,
  `deposit` decimal(15,2) DEFAULT NULL,
  `balance` decimal(15,2) NOT NULL,
  PRIMARY KEY (`transactionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `accounts` (
  `CardNumber` BIGINT(20) NOT NULL,
  `AccountNumber` BIGINT(20) NOT NULL,
  `UserName` VARCHAR(45) NOT NULL,
  `Password` VARCHAR(128) NOT NULL,
  `FirstName` VARCHAR(45) NOT NULL,
  `LastName` VARCHAR(45) NOT NULL,
  `PasswordSalt` VARBINARY(128) NOT NULL,
  `AvailableBalance` FLOAT(45) NOT NULL,
  `TotalBalance` FLOAT(45) NOT NULL,
  `TransferLimit` FLOAT(45) NOT NULL,
  `IsAdmin` SMALLINT(1) NOT NULL,
  PRIMARY KEY (`AccountNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;