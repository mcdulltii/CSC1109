package com.atm.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class AtmService {
    protected static Connection conn;
    private Account acc;
    private User user;
    private Withdraw withdrawal;
    private Deposit deposit;
    private Transfer transfer;
    private BufferedReader inputReader;
    private PrintWriter outputStream;
    private ArrayList<String> interactions;

    public AtmService(Account acc, User user, PrintWriter outputStream, BufferedReader inputReader) {
        this.acc = acc;
        AtmService.conn = new DBConnection().getConnection();
        this.user = user;
        this.withdrawal = new Withdraw(acc, conn);
        this.deposit = new Deposit(acc, conn);
        this.transfer = new Transfer(conn);
        this.outputStream = outputStream;
        this.inputReader = inputReader;
        this.interactions = new ArrayList<String>();
    }

    private String deposit(double amount) {
        return deposit.execute(acc, amount);
    }

    private String withdraw(double amount) throws InsufficientFundsException {
        return withdrawal.execute(acc, amount);
    }

    private String transfer(long transferAccNo, double amount) throws ExceedTransferLimitException, InsufficientFundsException {
        Account a2 = getTransferAccount(transferAccNo);
        return transfer.transferToAccount(acc, a2, amount);
    }

    // Get Available Balance and Total Balance from current account
    private double[] getBalance() {
        SQLQueries q = new SQLQueries();
        Account a = q.getAccountfromAccountNumber(Long.parseLong(acc.getAccountNumber()));
        acc.setAvailableBalance(a.getAvailableBalance());
        acc.setTotalBalance(a.getTotalBalance());   
        double[] balance = { acc.getAvailableBalance(), acc.getTotalBalance() };
        return balance;
    }

    // Create Account object based on accountNumber (For transfer)
    private static Account getTransferAccount(Long accountNumber) {
        SQLQueries q = new SQLQueries(conn);
        Account transferAccount = q.getAccountfromAccountNumber(accountNumber);
        return transferAccount;
    }

    public String getUserInput() {
        this.endLine();
        String s = "";
        try {
            s = inputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.strip();
    }

    private void endLine() {
        outputStream.println("END");
        outputStream.flush();
    }

    private void systemMenu() {
        outputStream.printf("%n---------- %s ----------%n", "Settings");
        outputStream.printf("| %-26s |%n", "(0) Return To Main Menu");
        outputStream.printf("| %-26s |%n", "(1) User Settings");
        outputStream.printf("| %-26s |%n", "(2) Account Settings");
        outputStream.printf("%s%n%n", "-".repeat(30));

        outputStream.print("Please enter an option: ");
    }

    private void userSystemMenu() {
        outputStream.printf("%n---------- %s ----------%n", "User Settings");
        outputStream.printf("| %-31s |%n", "(0) Return To Settings Menu");
        outputStream.printf("| %-31s |%n", "(1) Set Pin");
        outputStream.printf("%s%n%n", "-".repeat(35));

        outputStream.print("Please enter an option: ");
    }

    private void userSystemMenuOpt(int opt) {
        Settings settings = new Settings(user, conn);

        if (opt == 1) {
            String userinput;

            while (true) {
                outputStream.printf("%n---------- %s ----------%n",
                        "Set Pin");
                outputStream.printf("| %-25s |%n", "(0) Cancel Operation");
                outputStream.printf("%s%n%n", "-".repeat(29));

                outputStream.print("Please enter your new pin: ");
                userinput = getUserInput();
                if (Integer.parseInt(userinput) == -1)
                    break;
                try {
                    if (userinput.equals(new String("0")) && userinput.length() == 1) {
                        break;
                    } else if (userinput.length() != 6) {
                        outputStream.printf("Please enter a valid 6-digit numeric pin%n");
                        continue;
                    } else {
                        // check if user input is numeric
                        Integer.parseInt(userinput);
                    }

                    settings.setPinNumber(userinput);
                    outputStream.println("Pin Updated");
                    this.interactions.add("Changed pin number");
                    break;
                } catch (NumberFormatException e) {
                    outputStream.printf("Please enter a valid 6-digit numeric pin%n");
                    continue;
                }
            }
        }
    }

    private void accountSystemMenu() {
        outputStream.printf("%n---------- %s ----------%n", "Account Settings");
        outputStream.printf("| %-34s |%n", "(0) Return To Settings Menu");
        outputStream.printf("| %-34s |%n", "(1) Get Transfer Limit");
        outputStream.printf("| %-34s |%n", "(2) Set Transfer Limit");
        outputStream.printf("%s%n%n", "-".repeat(38));

        outputStream.print("Please enter an option: ");
    }

    private void accountSystemMenuOpt(int opt) {
        switch (opt) {
            case 1:
                outputStream.println("Your Transfer Limit is $" + acc.getTransferLimit());
                break;
            case 2:
                OptMenu: while (true) {
                    outputStream.printf("%n-------- %s ---------%n",
                            "Choose Transfer Limit ($)");
                    outputStream.printf("| %-40s |%n", "(0) Cancel Operation");
                    outputStream.printf("| %-40s |%n", "(1) 1000");
                    outputStream.printf("| %-40s |%n", "(2) 2000");
                    outputStream.printf("| %-40s |%n", "(3) 5000");
                    outputStream.printf("| %-40s |%n", "(4) 10000");
                    outputStream.printf("%s%n%n", "-".repeat(40));

                    outputStream.print("Please enter an option: ");

                    try {
                        int userinput = Integer.parseInt(getUserInput());

                        if (userinput == 0 || userinput == -1) {
                            break OptMenu;
                        } else if (userinput < 0 || userinput > 4) {
                            outputStream.println("Please enter a valid option");
                            continue;
                        }

                        Settings settings = new Settings(acc, conn);
                        double[] limits = { 1000, 2000, 5000, 10000 };
                        settings.setTransferLimit(limits[userinput - 1]);
                        outputStream.print("Transfer limit Updated");
                        this.interactions.add("Set transfer limit as $" + limits[userinput - 1]);
                        break;
                    } catch (NumberFormatException e) {
                        outputStream.println("Please enter a valid option");
                        continue;
                    }
                }
                break;
        }
    }

    // Returns amount based on user's selection or input
    // 
    // # Argument
    //
    // * 'action' - Deposit/Withdraw/Transfer action
    // 
    // # Return value
    //
    // Amount
    private double getInputAmount(String action) {
        double amount = 0.0;
        double[] amountOptions = { 10.0, 20.0, 50.0, 100.0, 500.0, 1000.0 };
        while (true) {
            outputStream.printf("%n- %s -%n",
                    "Choose Amount to " + action + " ($)");
                    
            outputStream.printf("| %-40s |%n", "(0) Cancel Operation");
            outputStream.printf("| %-40s |%n", "(1) 10");
            outputStream.printf("| %-40s |%n", "(2) 20");
            outputStream.printf("| %-40s |%n", "(3) 50");
            outputStream.printf("| %-40s |%n", "(4) 100");
            outputStream.printf("| %-40s |%n", "(5) 500");
            outputStream.printf("| %-40s |%n", "(6) 1000");
            outputStream.printf("| %-40s |%n", "(7) Custom Amount");
            outputStream.printf("%s%n%n", "-".repeat(40));
            outputStream.print("Please enter an option: ");
            try {
                int userinput = Integer.parseInt(getUserInput());
                if (userinput == 0 || userinput == -1)
                    break;
                if (userinput == 7) {
                    outputStream.println("Enter amount to " + action + ": $");
                    amount = Double.parseDouble(getUserInput());
                    if (amount == -1){
                        amount = 0;
                        return amount;
                    }
                    return amount;
                }else if (userinput < -1 || userinput > 7) {
                    outputStream.println("Please enter a valid option");
                    continue;
                }
                amount = amountOptions[userinput - 1];
                return amount;
            } catch (NumberFormatException e) {
                outputStream.println("Please enter a valid option");
                continue;
            }
        }
        return amount;
    }

    public void selectionMenu() {
        outputStream.printf("%n---------- %s ----------%n", "Available Services");
        outputStream.printf("| %-36s |%n", "(0) Exit");
        // User options
        outputStream.printf("| %-36s |%n", "(1) Deposit");
        outputStream.printf("| %-36s |%n", "(2) Withdraw");
        outputStream.printf("| %-36s |%n", "(3) Transfer");
        outputStream.printf("| %-36s |%n", "(4) View Account Balance");
        outputStream.printf("| %-36s |%n", "(5) Settings");
        outputStream.printf("| %-36s |%n", "(6) Help");
        outputStream.printf("%s%n%n", "-".repeat(40));

        outputStream.print("Please enter an option: ");
    }

    public void selection(int option) {
        switch (option) {
            case 0:
                break;
            case 1:
                // Deposit amount to current account
                double depositAmount = getInputAmount("Deposit");
                if (depositAmount != 0) {
                    try {
                        outputStream.println(deposit(depositAmount));
                        outputStream.println("Your Total Balance after deposit is: $" + acc.getTotalBalance());
                        this.interactions.add("Deposit: $" + depositAmount);
                    } catch (IllegalArgumentException e) {
                        outputStream.println(e.getMessage());
                    }
                }
                break;
            case 2:
                // Withdraw amount from current account
                double withdrawalAmount = getInputAmount("Withdraw");
                if (withdrawalAmount != 0) {
                    try {
                        outputStream.println(withdraw(withdrawalAmount));
                        outputStream.println("Your Total Balance after withdrawal is: $" + acc.getTotalBalance());
                        this.interactions.add("Withdraw: $" + withdrawalAmount);
                    } catch (IllegalArgumentException e) {
                        outputStream.println(e.getMessage());
                    } catch (InsufficientFundsException e) {
                        outputStream.println("\nSorry, but your account is short by: $" + e.getAmount());
                    }
                }
                break;
            case 3:
                // Transfer amount from current account to another account
                String userInput = "";
                SQLQueries q = new SQLQueries();
                outputStream.println("Please enter account number to transfer to: ");
                HashSet<String> accountNumbers = q.getAccountNumbers();
                // Check if account number exists in the database
                while (true){
                    userInput = getUserInput();
                    if (Long.parseLong(userInput) == -1)
                        break;
                    if(!accountNumbers.contains(userInput)){
                        outputStream.println("Please enter a valid account number.");
                        outputStream.println("Please enter account number to transfer to: ");
                        continue;
                    }
                    break;
                }
                long transferAccountNumber = Long.parseLong(userInput);
                if (transferAccountNumber == -1)
                    break;
                double transferAmount = getInputAmount("Transfer");
                if (transferAmount != 0) {
                    try {
                        transfer(transferAccountNumber, transferAmount);
                        outputStream.println("Your Total Balance after transfer is: $" + acc.getTotalBalance());
                        this.interactions.add("Transferred $" + transferAmount + " to " + transferAccountNumber);
                    } catch (IllegalArgumentException e) {
                        outputStream.println(e.getMessage());
                    } catch (ExceedTransferLimitException e) {
                        outputStream.println("\nSorry, but you exceeded your transfer limit by: $" + e.getAmount());
                    }catch (InsufficientFundsException e) {
                        outputStream.println("\nSorry, but your account is short by: $" + e.getAmount());
                    }
                }
                break;
            case 4:
                // Show Available Balance and Total Balance
                double[] balance = getBalance();
                outputStream.println("Your Available Balance is " + balance[0]);
                outputStream.println("Your Total Balance is " + balance[1]);
                break;
            case 5:
                while (true) {
                    systemMenu();

                    try {
                        int userinput = Integer.parseInt(getUserInput());

                        if (userinput == 0 || userinput == -1) {
                            break;
                        } else if (userinput == 1) {
                            userSystemMenu();
                            userinput = Integer.parseInt(getUserInput());
                            userSystemMenuOpt(userinput);
                            continue;
                        } else if (userinput == 2) {
                            accountSystemMenu();
                            userinput = Integer.parseInt(getUserInput());
                            accountSystemMenuOpt(userinput);
                            continue;
                        } else {
                            outputStream.println("Invalid choice! Please choose again!");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        outputStream.println("Invalid choice! Please choose again!");
                        continue;
                    }
                }
                break;
            case 6:
                // Help
                outputStream.println("Please contact the customer service hotline for any assistance.");
                break;
            default:
                if (option !=-1)
                    outputStream.println("Invalid choice! Please choose again!");
                break;
        }
    }

    public String getInteractions() {
        String receipt = new Date().toString() + "\nName: " + this.user.getFirstName() + " " + this.user.getLastName()
                + "\n";
        receipt += "Account number: " + this.user.getAccNo() + "\n\n<hr>\n";
        if (this.interactions.size() > 0) {
            receipt += String.join("\n", this.interactions);
        } else {
            receipt += "NIL";
        }
        return receipt;
    }
}
