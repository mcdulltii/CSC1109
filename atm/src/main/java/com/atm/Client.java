package com.atm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class ReceivedMessage {
    // Boolean to check if session is open
    public Boolean isOpen = false;
    // Server message prompt
    public String msg = "";
}

public class Client {
    private Socket socket;
    private PrintStream outputStream;
    private BufferedReader inputReader;
    // Boolean to check if client is in GUI or CLI mode
    private Boolean isCLI;
    // Number of login tries
    private int numTries;

    public Client(String ip, int port)  {
        this.numTries = 0;
        // Initialize client as CLI mode
        this.isCLI = true;
        try {
            this.socket = new Socket(ip, port);
        } catch (UnknownHostException e) {
            System.out.println("Please check host address input.");
        } catch (IllegalArgumentException e) {
            System.out.println("Port parameter is out of range.");
        } catch (IOException e) {
            System.out.println("Unable to create socket.");
        } 
    }

    public Client(String ip, int port, Boolean isCLI) {
        this.numTries = 0;
        this.isCLI = isCLI;
        try {
            this.socket = new Socket(ip, port);
        } catch (UnknownHostException e) {
            System.out.println("Please check host address input.");
        } catch (IllegalArgumentException e) {
            System.out.println("Port parameter is out of range.");
        } catch (IOException e) {
            System.out.println("Unable to create socket.");
        } 
    }

    public void startConnection() {
        // Initialize socket input and output streams
        try {
            outputStream = new PrintStream(socket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Unable to connect.");
        }
    }

    public ReceivedMessage receiveMessage() {
        String responseLine = "";
        ReceivedMessage recvMsg = new ReceivedMessage();
        try {
            // Retrieve server response
            if (inputReader.ready())
                responseLine = inputReader.readLine().trim();
            while (true) {
                // Retrieve server response till END or FIN indicators
                if (responseLine.equalsIgnoreCase("END")) {
                    // END indicating end of server message
                    recvMsg.isOpen = true;
                    return recvMsg;
                } else if (responseLine.equalsIgnoreCase("FIN")) {
                    // FIN indicating server closing session
                    recvMsg.isOpen = false;
                    return recvMsg;
                }
                // Actively retrieve incoming server messages
                if (responseLine != "") {
                    if (responseLine.length() > 3  &&
                        responseLine.substring(responseLine.length() - 3).equalsIgnoreCase("END")) {
                        String responseString = responseLine.substring(0, responseLine.length() - 3);
                        if (this.isCLI)
                            System.out.print(responseString);
                        recvMsg.isOpen = true;
                        recvMsg.msg += responseString + "\n";
                        return recvMsg;
                    } else {
                        if (this.isCLI)
                            System.out.println(responseLine);
                        recvMsg.msg += responseLine + "\n";
                    }
                }
                // Add delay to wait for new server messages
                Thread.sleep(50);
                if (inputReader.ready())
                    responseLine = inputReader.readLine();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Unable to read stream.");;
        }
        recvMsg.isOpen = true;
        return recvMsg;
    }

    public ReceivedMessage sendMessage(String msg) {
        // Send client message to server, and retrieve server response
        outputStream.println(msg);
        ReceivedMessage recvMsg = this.receiveMessage();
        outputStream.flush();
        return recvMsg;
    }

    public String sendUsernamePassword(String username, String password) {
        // Send username and password to server for client authentication
        this.sendMessage(username);
        ReceivedMessage recvMsg = this.sendMessage(password);
        this.numTries++;
        return recvMsg.msg;
    }

    public int getNumTries() {
        return this.numTries;
    }

    public String getInteractions() {
        // Retrieve ATM receipt from server
        ReceivedMessage recvMsg = this.receiveMessage();
        return recvMsg.msg;
    }

    public void close() {
        try {
            inputReader.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Unable to close socket.");
        }
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client("127.0.0.1", 7777);
        client.startConnection();
        // Receive server prompt
        client.sendMessage(null);
        client.receiveMessage();

        // Main server-client communication loop
        Boolean isOpen = true;
        while (isOpen) {
            String input = scanner.nextLine();
            if (input != null && input.length() > 0)
                isOpen = client.sendMessage(input).isOpen;
        }
        client.close();
        scanner.close();
    }
}
