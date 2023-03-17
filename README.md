# CSC1109 Project

1. Account Information (e.g. how many accounts, account numbers, etc)
2. Balance Check (e.g. remain balance, available balance, etc)
3. Authentication (e.g. password check / reset, etc)
4. Money Transfer(e.g. inter-account transfer, third-party transfer, etc)
5. Settings (e.g. transfer limit, overseas withdraw limit, etc)

## Dependencies

- Maven
- MySQL

## Setup

1. Install dependencies
2. Connect to MySQL server
  - Create database by `CREATE DATABASE oopasgdb;`
  - Grant user:password to access MySQL database by

    ```sql
    CREATE USER testAdmin@localhost IDENTIFIED BY 'password1';
    GRANT ALL PRIVILEGES ON oopasgdb.* TO testAdmin@localhost;
    ```
  - Create transactions table by `use oopasgdb;` and

    ```sql
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
    ```
  - Create accounts table

    ```sql
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
    ```

## Setup (Docker)

1. Start sql and server docker containers

  ```shell
  > ./start.sh
  ```

2. Run client script

  ```shell
  > make build
  > make run
  ```

3. Stop docker containers once done

  ```shell
  > ./stop.sh
  ```

## Class Diagram

```mermaid
classDiagram
    class CSVHandler {
        +read(filename, row)
        +write(filename, row)
    }
    ImportCSV <|-- CSVHandler
    class Account {
        -accountNumber
        -username
        -firstName
        -lastName
        -pinNumber
        -password
        -availableBalance
        -totalBalance
        -transferLimit
        -isAuthenticated
        -isAdmin
        +getUsername()
        +getAccountNumber()
        +getFirstName()
        +getLastName()
    }
    Balance <|-- Account
    class Balance {
        +getAvailableBalance()
        +setAvailableBalance()
        +getTotalBalance()
        +setTotalBalance()
    }
    Authenticate <|-- Account
    class Authenticate {
        +hasAuthenticated()
        +validatePinNumber()
        +validatePassword() -> Check hash
        %% +resetPassword() -> Tele bot
        %% +getOTP() -> Tele bot
        %% +validateOTP()
    }
    Transfer <|-- Balance
    Transfer <|-- Account
    class Transfer {
        +hasAvailableBalance()
        +belowTransferLimit()
        +transferToAccount() -> Update CSV for both accounts
    }
    Settings <|-- Account
    class Settings {
        +setTransferLimit()
        +getTransferLimit()
        %% +getPinNumber()
        %% +setPinNumber()
        +setPassword() -> secure hash
        +setUsername()
        +setFirstName()
        +setLastName()
    }
```

## Class Diagram UPDATE (Incomplete)

```mermaid
classDiagram
    class Account {
        -accountNumber
        -availableBalance
        -totalBalance
        -transferLimit
        +getAccountNumber()
        +getTransferLimit()
        +setTransferLimit(transferLimit: double)
        +getAvailableBalance()
        +setAvailableBalance(availableBalance: double)
        +getTotalBalance()
        +setTotalBalance(totalBalance: double)
        +getNewAccountNumber()
    }
    Authenticate <|-- Account
    class Authenticate {
        -numTries
        +hashString()
        +checkPassword()
        +getNumTries() 
    }
    Transaction <|-- Account
    class Transaction {
        -accountNumber
        -transactionDate
        -transactionDetails
        -chqNumber
        -valueDate
        -withdrawal
        -deposit
        -balance
        +getAccountNumber()
        +getTransactionDate()
        +getTransactionDetails()
        +getChqNumber()
        +getValueDate()
        +getWithdrawal()
        +getDeposit()
        +getBalance()
        +hasAvailableBalance()
        +belowTransferLimit()
        +transferToAccount(a1: Account, a2: Account, amount: double)
        +deposit(a1: Account, amount: double)
        +withdraw(a1: Account, amount: double)
    }
    Settings <|-- Account
    class Settings {
      +setTransferLimit(limit: double)
      +setPinNumber(pinNumber: String)
    }
    AccountSettings <|.. Settings: uses
    class AccountSettings{
        <<interface>> 
        +setTransferLimit(limit: double)
    }
    UserSettings <|.. Settings: uses
    class UserSettings{
        <<interface>> 
        +setPinNumber(pinNumber: String)
    }
    Account <|--AccUserObj
    User <|-- AccUserObj
    class AccUserObj{
        +getAccount()
        +getUser()
    }
    AtmService <|-- Account
    AtmService <|-- User
    AtmService <|-- Transaction
    class AtmService{
        +getUserInput()
        +systemMenu()
        +userSystemMenu()
        +userSystemMenuOutput()
        +accountSystemMenu()
        +accountSystemMenuOutput()
        +selectionMenu()
        +selection()
    }
    SQLQueries <|-- Account
    SQLQueries <|-- Settings
    SQLQueries <|-- Transaction
    class SQLQueries{
        +executeQueryTransactions(tr: Transaction)
        +executeQueryAccounts(a1: Account, a2: Account)
        +executeQuerySettings(ac: Account, field: String)
        +executeQuerySettings(user: User, field: String)
        +getAccountfromUsername(username: String)
        +getAccountfromAccountNumber(accountNumber: Long)
        +getPasswordfromUsername(username: String)
        +importAccounts()
        +executeQuery(query: String)
        +getConnection()
    }
    class User{
        -userId
        -AccNo
        -firstName
        -lastName
        -pinNumber
        -isAdmin
        +getUserId()
        +getAccNo()
        +getFirstName()
        +getLastName()
        +getPin()
        +setPin(pin: String)
        +getNewUserId()
    }
```
