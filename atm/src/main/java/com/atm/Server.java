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

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.start();
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
                // Call accept() to receive the next connection
                Socket socket = serverSocket.accept();

                // Pass the socket to the RequestHandler thread for processing
                ThreadClientHandler requestHandler = new ThreadClientHandler(socket);
                requestHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
        try {
            s.start(7777);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter admin password: ");
        String password = sc.next();
        if (password.equals("admin")) { // TODO: Authenticate
            System.out.println("Welcome Admin!");
            // TODO: List of Admin Actions
            System.out.println("Enter option: ");
            int choice = sc.nextInt();
        }
        // switch case all that for admin actions
        // one of actions is open server????
        System.out.println("i open server uwu");
    }
}

class ThreadClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Boolean authenticated = false;
    private String inputLine = "";

    public ThreadClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private String getUserInput() {
        String s = "";
        try {
            while (true) {
                s = in.readLine();
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
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // NOTE: ALL out.println MUST END WITH \n (no spaces) TO LET BUFFEREDREADER KNOW END-OF-INPUT
        // VV IMPT

        // Get client username and password
        while (!authenticated) {
            out.println("Enter username\n");
            String username = getUserInput();

            out.println("Enter password\n");
            String password = getUserInput();

            // TODO: Authenticate user
            authenticated = true;
        }

        // Print Menu
        out.println("Long list of stuff\n1.\n2.\n3.\nEnter input\n");

        // Some sample codes, entering ono closes the server, typing anything else
        // echoes itback
        try {
            inputLine = in.readLine();
            while (true) {
                if (inputLine != null && inputLine.length() > 0) {

                    if (inputLine.equalsIgnoreCase("terminate")) {
                        out.println("Connection Terminated.\n");
                        out.flush();
                        break;
                    }

                    out.println("From Server: " + inputLine);
                    out.println(""); // alternatively to \n you can do this but pls dont
                }
                inputLine = in.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
