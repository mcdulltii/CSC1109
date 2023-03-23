package com.atm.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class AtmService {
    private Account acc;
    private User user;
    private Transaction transaction;
    private BufferedReader inputReader;
    private PrintWriter outputStream;
    private ArrayList<String> interactions;

    public AtmService(Account acc, User user, PrintWriter outputStream, BufferedReader inputReader) {
        this.acc = acc;
        this.user = user;
        this.transaction = new Transaction(acc);
        this.outputStream = outputStream;
        this.inputReader = inputReader;
        this.interactions = new ArrayList<String>();
    }

    private String deposit(double amount) {
        return transaction.deposit(acc, amount);
    }

    private String withdraw(double amount) throws InsufficientFundsException {
        return transaction.withdraw(acc, amount);
    }

    private String transfer(long transferAccNo, double amount) throws InsufficientFundsException {
        Account a2 = getTransferAccount(transferAccNo);
        return transaction.transferToAccount(acc, a2, amount);
    }

    private double[] getBalance() {
        double[] balance = {acc.getAvailableBalance(), acc.getTotalBalance()};
        return balance;
    }

    // Create Account object based on accountNumber (For transfer)
    private static Account getTransferAccount(Long accountNumber) {
        SQLQueries q = new SQLQueries();
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
        Settings settings = new Settings(user);

        if (opt == 1) {
            String userinput;

            while (true) {
                outputStream.printf("%n---------- %s ----------%n",
                "Set Pin");
                outputStream.printf("| %-25s |%n", "(0) Cancel Operation");
                outputStream.printf("%s%n%n", "-".repeat(29));

                outputStream.print("Please enter your new pin: ");
                userinput = getUserInput();

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
                    outputStream.printf("%s%n%n", "-".repeat(44));

                    outputStream.print("Please enter an option: ");

                    try {
                        int userinput = Integer.parseInt(getUserInput());

                        if (userinput == 0) {
                            break OptMenu;
                        } else if (userinput < 0 || userinput > 4) {
                            outputStream.println("Please enter a valid option");
                            continue;
                        }

                        Settings settings = new Settings(acc);
                        double[] limits = {1000, 2000, 5000, 10000};
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
            case -1:
                // Return back to selection menu
                selectionMenu();
                break;
            case 0:
                break;
            case 1:
                // Deposit
                outputStream.print("Please enter an amount to deposit: $");
                double depositAmount = Double.parseDouble(getUserInput());
                try {
                    outputStream.println(deposit(depositAmount));
                    outputStream.println("Your Total Balance is after deposit is: $" + acc.getTotalBalance());
                    this.interactions.add("Deposit: $" + depositAmount);
                } catch (IllegalArgumentException e) {
                    outputStream.println(e.getMessage());
                }
                break;
            case 2:
                // Withdraw
                outputStream.print("Please enter an amount to withdraw: $");
                double withdrawalAmount = Double.parseDouble(getUserInput());
                try {
                    outputStream.println(withdraw(withdrawalAmount));
                    outputStream.println("Your Total Balance is after withdrawal is: $" + acc.getTotalBalance());
                    this.interactions.add("Withdraw: $" + withdrawalAmount);
                } catch (IllegalArgumentException e) {
                    outputStream.println(e.getMessage());
                } catch (InsufficientFundsException e) {
                    outputStream.println("\nSorry, but your account is short by: $"+ e.getAmount());
                }
                break;
            case 3:
                // Transfer
                outputStream.println("Please enter account number to transfer to: ");
                long transferAccountNumber = Long.parseLong(getUserInput());
                if (option != -1)
                    break;
                outputStream.println("Please enter amount to be transferred: ");
                double amount = Double.parseDouble(getUserInput());
                try {
                    transfer(transferAccountNumber, amount);
                    this.interactions.add("Transferred $" + amount + " to " + transferAccountNumber);
                } catch (IllegalArgumentException e) {
                    outputStream.println(e.getMessage());
                } catch (InsufficientFundsException e) {
                    outputStream.println("\nSorry, but your account is short by: $"+ e.getAmount());
                }
                break;
            case 4:
                // Account balance
                double[] balance = getBalance();
                outputStream.println("Your Available Balance is " + balance[0]);
                outputStream.println("Your Total Balance is " + balance[1]);
                break;
            case 5:
                while (true) {
                    systemMenu();

                    try {
                        int userinput = Integer.parseInt(getUserInput());

                        if (userinput == 0) {
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
                outputStream.println("Invalid choice! Please choose again!");
                break;
        }
    }

    public String getInteractions() {
        String receipt = new Date().toString() + "\nName: " + this.user.getFirstName() + " " + this.user.getLastName() + "\n";
        receipt += "Account number: " + this.user.getAccNo() + "\n\n<hr>\n";
        if (this.interactions.size() > 0) {
            receipt += String.join("\n", this.interactions);
        } else {
            receipt += "NIL";
        }
        return receipt;
    }
}
