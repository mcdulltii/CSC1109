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
        /* 
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter admin password: ");
        String password = sc.next();
        if (password == "admin") { // TODO: Authenticate
            // TODO: List of Admin Actions (copy from App.java im lazy rn)
            System.out.println("Welcome Admin!");
        }
        // switch case all that for admin actions
        // one of actions is open server????
        System.out.println("i open server uwu");
        */
        Server s = new Server();
        try {
            s.start(7777);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //NOTE: ALL out.println MUST END WITH \n TO LET BUFFEREDREADER KNOW END-OF-INPUT
        //VV IMPT I SPEND 5HRS ON THIS AND IM VERY SAD
        
        
        // Get client username and password
        /* 
        while (!authenticated) {
            try {
                out.println("Enter username\n");
                String username, password;
                while (true) {
                    inputLine = in.readLine();
                    if (inputLine.length() > 0)
                        username = inputLine;
                        break;
                }

                out.println("Enter password\n");
                while (true) {
                    inputLine = in.readLine();
                    if (inputLine.length() > 0)
                        password = inputLine;
                        break;
                }

                //TODO: Authenticate user
                authenticated = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
        //idk why the code above doesnt work sighs
        //Print Menu
        out.println("Long list of stuff\n1.\n2.\n3.\nEnter input\n");
        out.flush();
        
        
        //Some sample codes, entering ono closes the server, typing anything else echoes itback
        try {
            inputLine = in.readLine();
            while (true) {
                if (inputLine.length() > 0) {
                    
                    if ("ono".equals(inputLine)) {
                        out.println("Connection Terminated.\n");
                        out.flush();
                        break;
                    }

                    out.println("From Server: " + inputLine);
                    out.println(""); //alternatively to \n you can do this but pls dont
                    out.flush();
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
