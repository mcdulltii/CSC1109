package com.atm;

import java.io.BufferedReader;
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
        System.out.println("Enter admin password: ");
        String password = sc.next();
        if (password.equals("admin")) { // TODO: Authenticate
            System.out.println("Welcome Admin!");
            // TODO: List of Admin Actions
            System.out.println("Enter option: ");
            int choice = sc.nextInt();
        }
        // TODO: switch case all that for admin actions
        
        
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
        // NOTE: ALL out.println MUST END WITH \n (no spaces) TO LET BUFFEREDREADER KNOW END-OF-INPUT
        // VV IMPT

        // Get client username and password
        while (!authenticated) {
            outputStream.println("Enter username\n");
            String username = getUserInput();

            outputStream.println("Enter password\n");
            String password = getUserInput();

            // TODO: Authenticate user
            authenticated = true;
        }

        // Print Menu
        outputStream.println("Long list of stuff\n1.\n2.\n3.\nEnter input\n");

        // Some sample codes, entering terminate closes the server, typing anything else echoes it back
        try {
            inputLine = inputReader.readLine();
            while (true) {
                if (inputLine != null && inputLine.length() > 0) {

                    if (inputLine.equalsIgnoreCase("terminate")) {
                        outputStream.println("Connection Terminated.\n");
                        outputStream.flush();
                        break;
                    }

                    outputStream.println("From Server: " + inputLine);
                    outputStream.println(""); // alternatively to \n you can do this but pls dont
                }
                inputLine = inputReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputReader.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
