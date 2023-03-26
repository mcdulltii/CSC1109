package com.atm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.Scanner;

import com.atm.backend.AccUserObj;
import com.atm.backend.Account;
import com.atm.backend.AtmService;
import com.atm.backend.Authenticate;
import com.atm.backend.DBConnection;
import com.atm.backend.SQLQueries;
import com.atm.backend.User;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Server extends Thread {
    private ServerSocket serverSocket;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            this.start();
        } catch (IllegalThreadStateException e){
            System.out.println("Thread has already started.");
        } catch (IllegalArgumentException e){
            System.out.println("Port parameter is out of range");
        } catch (IOException e) {
            System.out.println("Unable to start server.");;
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Unable to close server.");;
        }
        this.interrupt();
    }

    @Override
    public void run() {
        // Listen to incoming client connections
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ThreadClientHandler handler = new ThreadClientHandler(socket);
                handler.start();
            } catch (IOException e) {
                System.out.println("Error when waiting for connection.");;
            } catch (IllegalThreadStateException e){
                System.out.println("Thread has already started.");
            }
        }
    }

    public static void main(String[] args) {
        // Retrieve commandline arguments
        ArgumentParser parser = ArgumentParsers.newFor("Server").build()
                .defaultHelp(true)
                .description("ATM Server backend");
        parser.addArgument("--partial").action(Arguments.storeTrue())
                .help("Flag to import transactions table partially");
        parser.addArgument("-P", "--port")
                .setDefault(7777)
                .help("Specify which port to expose server on");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(0);
        }

        Server s = new Server();
        s.start(ns.getInt("port"));

        // Fill account database
        SQLQueries q = new SQLQueries();
        String adminPassword = null;
        try {
            // Import accounts table
            q.importAccounts();
        } catch (FileNotFoundException e) {
            System.out.println("Import Accounts File not found.");
        }

        Boolean isPartial = ns.getBoolean("partial");
        try {
            // Import transactions table
            if (args.length > 0 && isPartial)
                q.importTransactions(true);
            else
                q.importTransactions(false);
        } catch (FileNotFoundException e) {
            System.out.println("Import Transactions File not found.");
        }
        // Import admin account
        adminPassword = q.importAdminAccount();
        Scanner sc = new Scanner(System.in);
        Connection conn = new DBConnection().getConnection();
        Authenticate au = new Authenticate(conn);
        login: while (true) {
            System.out.print("Enter admin password: ");
            String password = sc.nextLine().strip();
            byte[] passwordSalt = q.getAdminPasswordSalt();
            if (au.hashString(password, passwordSalt).equals(adminPassword))
            {
                System.out.println("Welcome Admin!");
                while (true) {
                    System.out.println("Available Options:\n(1) Display first 100 accounts\n(2) Display first 100 transactions\n(0) Logout admin");
                    System.out.print("Enter option: ");
                    int choice = sc.nextInt();
                    sc.nextLine();
                    switch (choice) {
                        case 1:
                            printAllAccounts();
                            break;
                        case 2:
                            printAllTransactions();
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
            int count = 0;
            while (true) {
                String row = br.readLine();
                if (row == null || count > 100)
                    break;
                String[] data = row.split(",");
                System.out.println(String.format("%20s %20s %20s %20.2f %20.2f", data[0], data[3], data[4],
                        Float.parseFloat(data[6]), Float.parseFloat(data[7])));
                count++;
            }
            br.close();
        } catch (FileNotFoundException e){
            System.out.println("Accounts CSV File not found.");
        } catch (IOException e) {
            System.out.println("Unable to read file.");;
        } catch (NumberFormatException e){
            System.out.println("Unable to parse string.");
        }
    }

    private static void printAllTransactions() {
        System.out.println(String.format("%20s %20s %20s %20s %20s %20s %20s %20s %20s", "Transaction ID", "Account Number","Transaction Date","Transaction Details","Chq Number","Value Date","Withdrawal","Deposit","Balance"));
        try {
            BufferedReader br = new BufferedReader(new FileReader("atm/res/transactions_new.csv"));
            br.readLine(); // skip headers
            int count = 0;
            while (true) {
                String row = br.readLine();
                if (row == null || count > 100)
                    break;
                String[] data = row.split(",");
                System.out.println(String.format("%20s %20s %20s %20s %20s %20s %20.2f %20.2f %20.2f", data[0], data[1], data[2], data[3], data[4], data[5],
                        Float.parseFloat(data[6]), Float.parseFloat(data[7]), Float.parseFloat(data[8])));
                count++;
            }
            br.close();
        } catch (FileNotFoundException e){
            System.out.println("Transactions CSV File not found.");
        } catch (IOException e) {
            System.out.println("Unable to read file.");;
        } catch (NumberFormatException e){
            System.out.println("Unable to parse string.");
        }
    }
}

class ThreadClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter outputStream;
    private BufferedReader inputReader;
    // Boolean to check if client is authenticated
    private Boolean authenticated = false;

    public ThreadClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private String getUserInput() {
        // Retrieve user input from client response
        endLine();
        String s = "";
        try {
            s = inputReader.readLine();
        } catch (IOException e) {
            System.out.println("Unable to read user input.");;
        }
        return s.strip();
    }

    private void endLine() {
        // Send END indicator to indicate end of server message
        outputStream.println("END");
        outputStream.flush();
    }

    public void run() {
        try {
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Unable to create stream. Please check socket connection.");
            return;
        }

        // Send server prompt
        getUserInput();

        // Welcome Message
        outputStream.println("");
        outputStream.printf("%s%n", "-".repeat(32));
        outputStream.printf("|        Welcome to ATM!       |%n");
        outputStream.printf("%s%n%n", "-".repeat(32));

        // Get client card number and password
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
                // Check if authentication succeeds
                if (au.checkPassword(cardNumber, password)) {
                    // Set user based on card number input
                    obj = getCurrentUserAcc(cardNumber);
                    acc = obj.getAccount();
                    user = obj.getUser();
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

        // User has authenticated, goto main server prompt loop
        if (authenticated) {
            outputStream.println("User authenticated");
            AtmService svc = new AtmService(acc, user, outputStream, inputReader);
            int userinput = 0;

            do {
                // Server prompt a user selection menu
                svc.selectionMenu();
                try {
                    // Retrieve user input from client response
                    userinput = Integer.parseInt(svc.getUserInput());
                    // If client responds to return back to menu
                    if (userinput == -1) {
                        svc.selectionMenu();
                    }
                    // Handle user input selection
                    svc.selection(userinput);
                } catch (NumberFormatException e) {
                    outputStream.println("Invalid choice! Please choose again!");
                }
            } while (userinput != 0);

            // if here, user has prompted to terminate connection
            // Thank You Message
            outputStream.println("Thank You and Have a Nice Day!");
            endSession(outputStream);

            // Send receipt
            outputStream.println(svc.getInteractions());
            this.endLine();
        }

        // Close connection
        try {
            inputReader.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection.");;
        }
    }

    // Create Account object based on card number input
    private AccUserObj getCurrentUserAcc(String cardNumber) {
        SQLQueries q = new SQLQueries();
        AccUserObj currentUserAcc = q.getAccountfromCardNumber(cardNumber);
        return currentUserAcc;
    }

    private void endSession(PrintWriter outputStream) {
        // Send FIN indicator to indicate end of session
        outputStream.println("FIN");
    }
}
