package com.atm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import com.atm.backend.AccUserObj;
import com.atm.backend.Account;
import com.atm.backend.AtmService;
import com.atm.backend.Authenticate;
import com.atm.backend.DBConnection;
import com.atm.backend.SQLQueries;
import com.atm.backend.User;
import com.atm.frontend.AdminTable;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Server extends Thread {
    private ServerSocket serverSocket;
    // Starts the server at the port
    // 
    // Arguments
    // 
    // \param port port number for the server to be hosted on
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            this.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("Thread has already started.");
        } catch (IllegalArgumentException e) {
            System.out.println("Port parameter is out of range");
        } catch (IOException e) {
            System.out.println("Unable to start server.");
            ;
        }
    }
    // Stops the server
    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Unable to close server.");
        }
        this.interrupt();
    }

    @Override
    public void run() {
        /// Listen to incoming client connections
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ThreadClientHandler handler = new ThreadClientHandler(socket);
                handler.start();
            } catch (IOException e) {
                System.out.println("Error when waiting for connection.");
            } catch (IllegalThreadStateException e) {
                System.out.println("Thread has already started.");
            }
        }
    }

    public static void main(String[] args) {
        /// Retrieve commandline arguments
        ArgumentParser parser = ArgumentParsers.newFor("Server").build()
                .defaultHelp(true)
                .description("ATM Server backend");
        parser.addArgument("--partial").action(Arguments.storeTrue())
                .help("Flag to import transactions table partially");
        parser.addArgument("-P", "--port")
                .type(Integer.class)
                .setDefault(7777)
                .help("Specify which port to expose server on");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(0);
        }

        // Start the server
        Server s = new Server();
        s.start(ns.getInt("port"));

        /// Fill account database
        SQLQueries q = new SQLQueries();
        String adminPassword = null;
        try {
            /// Import accounts table
            q.importAccounts();
        } catch (FileNotFoundException e) {
            System.out.println("Import Accounts File not found.");
        }

        // Optionally only partially retrieves transaction details
        Boolean isPartial = ns.getBoolean("partial");
        try {
            /// Import transactions table
            if (args.length > 0 && isPartial)
                q.importTransactions(true);
            else
                q.importTransactions(false);
        } catch (FileNotFoundException e) {
            System.out.println("Import Transactions File not found.");
        }

        /// Import admin account
        adminPassword = q.importAdminAccount();
        Scanner sc = new Scanner(System.in);
        Connection conn = new DBConnection().getConnection();
        Authenticate au = new Authenticate(conn);
        login: while (true) {
            // Login for admin
            System.out.print("Enter admin password: ");
            String password = sc.nextLine().strip();
            byte[] passwordSalt = q.getAdminPasswordSalt();
            if (au.hashString(password, passwordSalt).equals(adminPassword)) {
                // Admin is successfully logged in
                System.out.println("Welcome Admin!");
                while (true) {
                    // Print options for admin to use
                    System.out.println(
                            "Available Options:\n(1) Display first 100 accounts\n(2) Display first 100 transactions\n(3) Display account details from Card Number\n(4) Display first 100 transactions from Card Number\n(0) Logout admin");
                    System.out.print("Enter option: ");
                    // Get admin's choice of function
                    int choice = sc.nextInt();
                    sc.nextLine();
                    switch (choice) {
                        case 1:
                            // Print account details of all accounts
                            printAllAccounts();
                            break;
                        case 2:
                            // Print transaction details of latest 100 transactions
                            printAllTransactions();
                            break;
                        case 3:
                            // Print account details of a user 
                            System.out.print("Enter card number: ");
                            Long lng = sc.nextLong();
                            printAccountFromCardNo(lng);
                            break;
                        case 4:
                            // Print transaction history of a user
                            System.out.print("Enter card number: ");
                            Long lng2 = sc.nextLong();
                            printTransactionFromCardNo(lng2);
                            break;
                        case 0:
                            // Logs out the admin, but keeps the server running
                            System.out.println("\nLogout Successful!\n");
                            continue login;
                        default:
                            // Invalid Option
                            System.out.println("\nInvalid choice! Please choose again!\n");
                    }
                }
            }
        }
    }

    // Prints all accounts in a separate JFrame Table 
    private static void printAllAccounts() {
        SQLQueries q = new SQLQueries();
        ArrayList<String[]> data = q.getTopAccountsForAdmin("");
        AdminTable adminTable = new AdminTable();
        adminTable.displayTable("Account Details", data);
        System.out.println("\nAccount data shown in a new tab.\n");
    }

    // Prints all transactions in a separate JFrame Table
    private static void printAllTransactions() {
        SQLQueries q = new SQLQueries();
        ArrayList<String[]> data = q.getTopTransactionsForAdmin("");
        AdminTable adminTable = new AdminTable();
        adminTable.displayTable("Transaction Details", data);
        System.out.println("\nTransaction data shown in a new tab.\n");
    }

    // Prints account details for a specific user, identified by their card number
    // 
    // # Arguments
    // 
    // \param lng Card Number
    private static void printAccountFromCardNo(Long lng) {
        SQLQueries q = new SQLQueries();
        ArrayList<String[]> data = q.getTopAccountsForAdmin(Long.toString(lng));
        AdminTable adminTable = new AdminTable();
        adminTable.displayTable("Account Details", data);
        System.out.println("\nAccount data shown in a new tab.\n");
    }

    // Prints transaction history for a specific user, identified by their card number
    // 
    // # Arguments
    // 
    // \param lng Card Number
    private static void printTransactionFromCardNo(Long lng) {
        SQLQueries q = new SQLQueries();
        ArrayList<String[]> data = q.getTopTransactionsForAdmin(Long.toString(lng));
        AdminTable adminTable = new AdminTable();
        adminTable.displayTable("Transaction Details", data);
        System.out.println("\nTransaction data shown in a new tab.\n");
    }
}

// Thread for handling multiple clients, one client per thread
class ThreadClientHandler extends Thread {
    private Socket clientSocket;
    //Stream of text that the Server sends to the Client
    private PrintWriter outputStream;
    //Reader to read text that the Client sends to the Server
    private BufferedReader inputReader;
    /// Boolean to check if client is authenticated
    private Boolean authenticated = false;

    // Default constructor
    public ThreadClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    /// Retrieve user input from client
    private String getUserInput() {
        endLine();
        String s = "";
        try {
            s = inputReader.readLine();
        } catch (IOException e) {
            System.out.println("Unable to read user input.");
        }
        return s.strip();
    }

    /// Send END indicator to client to indicate end of server message
    private void endLine() {
        outputStream.println("END");
        outputStream.flush();
    }

    public void run() {
        try {
            //setup for socket
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Unable to create stream. Please check socket connection.");
            return;
        }

        /// Send server prompt
        getUserInput();

        /// Welcome Message
        outputStream.println("");
        outputStream.printf("%s%n", "-".repeat(32));
        outputStream.printf("|        Welcome to ATM!       |%n");
        outputStream.printf("%s%n%n", "-".repeat(32));

        /// Get client card number and password
        AccUserObj obj = null;
        Account acc = null;
        User user = null;
        Connection conn = new DBConnection().getConnection();
        Authenticate au = new Authenticate(conn);
        while (!authenticated) {
            outputStream.print("Enter Card Number: ");
            String cardNumber = getUserInput();

            outputStream.print("Enter password: ");
            String password = getUserInput();

            if (cardNumber.length() != 0 && password.length() != 0) {
                /// Check if authentication succeeds
                if (au.checkPassword(cardNumber, password)) {
                    /// Set user based on card number input
                    obj = getCurrentUserAcc(cardNumber);
                    acc = obj.getAccount();
                    user = obj.getUser();
                    authenticated = true;
                    break;
                }
            }

            /// 3 times timeout
            int numTries = au.getNumTries();
            outputStream.println("Password incorrect, try again.");
            outputStream.printf("You have %d tries left. ", 3 - numTries);
            if (numTries >= 3) {
                outputStream.println("Terminating session.");
                endSession(outputStream);
            } else
                outputStream.println("\n");
        }

        /// User has authenticated, goto main server prompt loop
        if (authenticated) {
            outputStream.println("User authenticated");
            AtmService svc = new AtmService(acc, user, outputStream, inputReader);
            int userinput = 0;

            do {
                /// Server prompt a user selection menu
                svc.selectionMenu();
                try {
                    /// Retrieve user input from client response
                    userinput = Integer.parseInt(svc.getUserInput());
                    /// Handle user input selection
                    svc.selection(userinput);
                } catch (NumberFormatException e) {
                    outputStream.println("Invalid choice! Please choose again!");
                }
            } while (userinput != 0);

            /// if here, user has prompted to terminate connection
            /// Thank You Message
            outputStream.println("Thank You and Have a Nice Day!");
            endSession(outputStream);

            /// Send receipt
            outputStream.println(svc.getInteractions());
            this.endLine();
        }

        /// Close connection
        try {
            inputReader.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection.");
        }
    }

    // Create Account object based on card number input
    //
    // # Arguments
    // 
    // \param cardNumber Card Number of User
    //
    // # Return Value
    //
    // \return Account object of user
    private AccUserObj getCurrentUserAcc(String cardNumber) {
        SQLQueries q = new SQLQueries();
        AccUserObj currentUserAcc = q.getAccountfromCardNumber(cardNumber);
        return currentUserAcc;
    }

    // Send FIN indicator to indicate end of session
    //
    // # Arguments
    // 
    // \param outputStream Stream of text that the Server sends to the Client
    private void endSession(PrintWriter outputStream) {
        outputStream.println("FIN");
    }
}
