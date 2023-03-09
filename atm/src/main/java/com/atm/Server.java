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

        Scanner sc = new Scanner(System.in);
        login: while (true) {
            System.out.println("Enter admin password: ");
            String password = sc.next();
            if (password.equals("admin")) // TODO: Authenticate
            {
                System.out.println("Welcome Admin!");
                while (true) {
                    System.out.println("Available Options:\n(1) Display all accounts\n(0) Logout admin");
                    System.out.println("Enter option: ");
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

    public static void printAllAccounts() {
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
    private String inputLine = "";

    public ThreadClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private String getUserInput() {
        String s = "";
        try {
            while (true) {
                s = inputReader.readLine();
                if (s != null && s.length() > 0)
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void run() {
        try {
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // NOTE: ALL out.println MUST END WITH \n (no spaces) TO LET BUFFEREDREADER KNOW
        // END-OF-INPUT

        // Fill account database
        SQLQueries q = new SQLQueries();
        try {
            q.importAccounts();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get client username and password
        Account user = null;
        while (!authenticated) {
            outputStream.println("Enter username\n");
            String username = getUserInput();

            outputStream.println("Enter password\n");
            String password = getUserInput();

            // TODO: Authenticate user
            user = authenticateUser();
            authenticated = true;
        }

        SelectionMenu: while (true) {
            // User options
            outputStream.println("Available Services:\n");
            outputStream
                    .println("(1) Deposit\n(2) Withdraw\n(3) Transfer\n(4) View Account Balance\n(5) Help\n(0) Exit\n");
            outputStream.println("Please enter an option:\n");
            try {
                Transaction transaction = new Transaction(user);
                int userinput = Integer.parseInt(getUserInput());
                switch (userinput) {
                    case 0:
                        break SelectionMenu;
                    case 1:
                        // Deposit
                        outputStream.println("Please enter an amount to deposit:\n");
                        double depositAmount = Double.parseDouble(getUserInput());
                        transaction.deposit(user, depositAmount);
                        break;
                    case 2:
                        // Withdraw
                        outputStream.println("Please enter an amount to withdraw:\n");
                        double withdrawalAmount = Double.parseDouble(getUserInput());
                        transaction.withdraw(user, withdrawalAmount);
                        break;
                    case 3:
                        // Transfer
                        outputStream.println("Please enter account number to transfer to:\n");
                        long transferAccountNumber = Long.parseLong(getUserInput());
                        outputStream.println("Please enter amount to be transferred:\n");
                        double amount = Double.parseDouble(getUserInput());
                        Account a2 = getTransferAccount(transferAccountNumber);
                        transaction.transferToAccount(user, a2, amount);

                    case 4:
                        outputStream.println("Your Available Balance is " + user.getAvailableBalance() + "\n");
                        outputStream.println("Your Total Balance is " + user.getTotalBalance() + "\n");
                        break;
                    case 5:
                        outputStream.println("Please contact the customer service hotline for any assistance.\n");
                    default:
                        outputStream.println("Invalid choice! Please choose again!\n");
                        break;
                }
            } catch (NumberFormatException e) {
                outputStream.println("Invalid choice! Please choose again!\n");
            }
        }

        // if here, user has prompted to terminate connection
        // Thank You Message
        outputStream.println("Thank You and Have a Nice Day!\n");

        // Close connection
        try {
            inputReader.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Account authenticateUser() {
        Account account = new Account(0.0, 0.0, 0.0, true);
        return account;
    }

    // Create Account object based on username input
    public static Account getCurrentUser(String username) {
        SQLQueries q = new SQLQueries();
        Account currentUser = q.getAccountfromUsername(username);
        return currentUser;
    }

    // Create Account object based on accountNumber (For transfer)
    public static Account getTransferAccount(Long accountNumber) {
        SQLQueries q = new SQLQueries();
        Account transferAccount = q.getAccountfromAccountNumber(accountNumber);
        return transferAccount;
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
