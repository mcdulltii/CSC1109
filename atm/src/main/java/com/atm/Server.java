package com.atm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread {
    private ServerSocket serverSocket;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ThreadClientHandler handler = new ThreadClientHandler(socket);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.start(7777);

        // Fill account database
        SQLQueries q = new SQLQueries();
        try {
            q.importAccounts();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);
        login: while (true) {
            System.out.println("Enter admin password:");
            String password = sc.next();
            if (password.equals("admin")) // TODO: Authenticate
            {
                System.out.println("Welcome Admin!");
                while (true) {
                    System.out.println("Available Options:\n(1) Display all accounts\n(0) Logout admin");
                    System.out.println("Enter option:");
                    int choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            printAllAccounts();
                            break;
                        case 0:
                            System.out.println("Logout Successful!");
                            continue login;
                        default:
                            System.out.println("Invalid choice! Please choose again!");
                    }
                }
            }

        }

    }

    private static void printAllAccounts() {
        System.out.println(String.format("%20s %20s %20s %20s %20s", "Account Number", "First Name", "Last Name",
                "Total Balance", "Available Balance"));
        try {
            BufferedReader br = new BufferedReader(new FileReader("atm/res/accounts.csv"));
            br.readLine(); // skip headers
            while (true) {
                String row = br.readLine();
                if (row == null)
                    break;
                String[] data = row.split(",");
                System.out.println(String.format("%20s %20s %20s %20.2f %20.2f", data[0], data[3], data[4],
                        Float.parseFloat(data[6]), Float.parseFloat(data[7])));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ThreadClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter outputStream;
    private BufferedReader inputReader;
    private Boolean authenticated = false;

    public ThreadClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private String getUserInput() {
        endLine();
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

    public void run() {
        try {
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Send server prompt
        getUserInput();

        // Welcome Message
        outputStream.println("");
        outputStream.printf("%s%n", "-".repeat(32));
        outputStream.printf("|        Welcome to ATM!       |%n");
        outputStream.printf("%s%n%n", "-".repeat(32));

        // Get client username and password
        Account user = null;
        Authenticate au = new Authenticate();
        while (!authenticated) {
            outputStream.print("Enter username: ");
            String username = getUserInput();

            outputStream.print("Enter password: ");
            String password = getUserInput();

            if (username.length() != 0 && password.length() != 0) {
                if (au.checkPassword(username, password)) {
                    // Set user based on username input
                    user = getCurrentUser(username);
                    authenticated = true;
                    break;
                }
            }

            // 3 times timeout
            int numTries = au.getNumTries();
            outputStream.println("Password incorrect, try again.");
            outputStream.printf("You have %d tries left. ", 3 - numTries);
            if (numTries >= 3) {
                outputStream.println("Terminating session.");
                endSession(outputStream);
            } else
                outputStream.println("\n");
        }

        if (authenticated) {
            outputStream.println("User authenticated");
            selectionMenu(user, outputStream);
        }

        // Close connection
        try {
            inputReader.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create Account object based on username input
    private static Account getCurrentUser(String username) {
        SQLQueries q = new SQLQueries();
        Account currentUser = q.getAccountfromUsername(username);
        return currentUser;
    }

    // Create Account object based on accountNumber (For transfer)
    private static Account getTransferAccount(Long accountNumber) {
        SQLQueries q = new SQLQueries();
        Account transferAccount = q.getAccountfromAccountNumber(accountNumber);
        return transferAccount;
    }

    private void selectionMenu(Account user, PrintWriter outputStream) {
        SelectionMenu: while (true) {
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

            outputStream.println("Please enter an option: ");

            try {
                Transaction transaction = new Transaction(user);
                int userinput = Integer.parseInt(getUserInput());
                
                switch (userinput) {
                    case 0:
                        break SelectionMenu;
                    case 1:
                        // Deposit
                        outputStream.print("Please enter an amount to deposit: $");
                        double depositAmount = Double.parseDouble(getUserInput());
                        transaction.deposit(user, depositAmount);
                        outputStream.println("Your Total Balance is after deposit is: $" + user.getTotalBalance());
                        break;
                    case 2:
                        // Withdraw
                        outputStream.print("Please enter an amount to withdraw: $");
                        double withdrawalAmount = Double.parseDouble(getUserInput());
                        transaction.withdraw(user, withdrawalAmount);
                        outputStream.println("Your Total Balance is after withdrawal is: $" + user.getTotalBalance());
                        break;
                    case 3:
                        // Transfer
                        outputStream.println("Please enter account number to transfer to: ");
                        long transferAccountNumber = Long.parseLong(getUserInput());
                        outputStream.println("Please enter amount to be transferred: ");
                        double amount = Double.parseDouble(getUserInput());
                        Account a2 = getTransferAccount(transferAccountNumber);
                        transaction.transferToAccount(user, a2, amount);
                        break;
                    case 4:
                        // Account balance
                        outputStream.println("Your Available Balance is " + user.getAvailableBalance());
                        outputStream.println("Your Total Balance is " + user.getTotalBalance());
                        break;
                    case 5:
                        // Settings
                        // TODO: Add settings functions
                        break;
                    case 6:
                        // Help
                        outputStream.println("Please contact the customer service hotline for any assistance.");
                        break;
                    default:
                        outputStream.println("Invalid choice! Please choose again!");
                        break;
                }
            } catch (NumberFormatException e) {
                outputStream.println("Invalid choice! Please choose again!");
            }
        }

        // if here, user has prompted to terminate connection
        // Thank You Message
        outputStream.println("Thank You and Have a Nice Day!");
        endSession(outputStream);
    }

    private void endSession(PrintWriter outputStream) {
        outputStream.println("FIN");
    }
}

/*
 * // Some sample codes, entering terminate closes the server, typing anything
 * else echoes it back
 * try {
 * inputLine = inputReader.readLine();
 * while (true) {
 * if (inputLine != null && inputLine.length() > 0) {
 * 
 * if (inputLine.equalsIgnoreCase("terminate")) {
 * outputStream.println("Connection Terminated.\n");
 * outputStream.flush();
 * break;
 * }
 * 
 * outputStream.println("From Server: " + inputLine);
 * outputStream.println(""); // alternatively to \n you can do this but pls dont
 * }
 * inputLine = inputReader.readLine();
 * }
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 */
