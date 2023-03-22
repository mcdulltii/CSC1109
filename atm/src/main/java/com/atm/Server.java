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

import com.atm.backend.AccUserObj;
import com.atm.backend.Account;
import com.atm.backend.AtmService;
import com.atm.backend.Authenticate;
import com.atm.backend.SQLQueries;
import com.atm.backend.User;

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
        Server s = new Server();
        s.start(7777);

        // Fill account database
        SQLQueries q = new SQLQueries();
        String adminPassword = null;
        try {
            q.importAccounts();
            adminPassword = q.importAdminAccount();
        } catch (FileNotFoundException e) {
            System.out.println("Import Accounts File not found.");
        }
        try {
            q.importTransactions();
            adminPassword = q.importAdminAccount();
        } catch (FileNotFoundException e) {
            System.out.println("Import Transactions File not found.");
        }
        Scanner sc = new Scanner(System.in);
        Authenticate au = new Authenticate();
        login: while (true) {
            System.out.print("Enter admin password: ");
            String password = sc.nextLine().strip();
            if (au.hashString(password).equals(adminPassword))
            {
                System.out.println("Welcome Admin!");
                while (true) {
                    System.out.println("Available Options:\n(1) Display all accounts\n(2) Display all transactions\n(0) Logout admin");
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
            while (true) {
                String row = br.readLine();
                if (row == null)
                    break;
                String[] data = row.split(",");
                System.out.println(String.format("%20s %20s %20s %20.2f %20.2f", data[0], data[3], data[4],
                        Float.parseFloat(data[6]), Float.parseFloat(data[7])));
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
            while (true) {
                String row = br.readLine();
                if (row == null)
                    break;
                String[] data = row.split(",");
                System.out.println(String.format("%20s %20s %20s %20s %20s %20s %20.2f %20.2f %20.2f", data[0], data[1], data[2], data[3], data[4], data[5],
                        Float.parseFloat(data[6]), Float.parseFloat(data[7]), Float.parseFloat(data[8])));
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
            System.out.println("Unable to read user input.");;
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

        // Get client username and password
        AccUserObj obj = null;
        Account acc = null;
        User user = null;
        Authenticate au = new Authenticate();
        while (!authenticated) {
            outputStream.print("Enter username: ");
            String username = getUserInput();

            outputStream.print("Enter password: ");
            String password = getUserInput();

            if (username.length() != 0 && password.length() != 0) {
                if (au.checkPassword(username, password)) {
                    // Set user based on username input
                    obj = getCurrentUserAcc(username);
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

        if (authenticated) {
            outputStream.println("User authenticated");
            AtmService svc = new AtmService(acc, user, outputStream, inputReader);
            int userinput = 0;

            do {
                svc.selectionMenu();
                try {
                    userinput = Integer.parseInt(svc.getUserInput());
                    svc.selection(userinput);
                } catch (NumberFormatException e) {
                    outputStream.println("Invalid choice! Please choose again!");
                }
            } while (userinput != 0);

            // if here, user has prompted to terminate connection
            // Thank You Message
            outputStream.println("Thank You and Have a Nice Day!");
            endSession(outputStream);
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

    // Create Account object based on username input
    private static AccUserObj getCurrentUserAcc(String username) {
        SQLQueries q = new SQLQueries();
        AccUserObj currentUserAcc = q.getAccountfromUsername(username);
        return currentUserAcc;
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
