# CSC1109 Project

1. Account Information (e.g. how many accounts, account numbers, etc)
2. Balance Check (e.g. remain balance, available balance, etc)
3. Authentication (e.g. password check / reset, etc)
4. Money Transfer(e.g. inter-account transfer, third-party transfer, etc)
5. Settings (e.g. transfer limit, overseas withdraw limit, etc)

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
