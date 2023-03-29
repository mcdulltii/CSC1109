package com.atm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

class ReceivedMessage {
    /// Boolean to check if session is open
    public Boolean isOpen = false;
    /// Server message prompt
    public String msg = "";
}

public class Client {
    private Socket socket;
    //Stream of text that the Client sends to the server
    private PrintStream outputStream;
    //Reader to read text that the Server sends to the client
    private BufferedReader inputReader;
    /// Boolean to check if client is in GUI or CLI mode
    private Boolean isCLI;
    /// Number of login tries
    private int numTries;

    //Default Constructer for Client for CLI
    public Client(String ip, int port)  {
        this.numTries = 0;
        /// Initialize client as CLI mode
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

    //Constructor for Client for GUI
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

    // Creates the outputStream and inputReader for the Client
    //
    // # Return Value
    // 
    // \return True if connection is valid and started, otherwise false
    public Boolean startConnection() {
        /// Initialize socket input and output streams
        try {
            outputStream = new PrintStream(socket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
            return false;
        } catch (NullPointerException e) {
            System.out.println("Unable to connect to server.");
            return false;
        }
        return true;
    }

    // Handles message sent by Server 
    //
    // # Return Value
    //
    // \return ReceivedMessage Object containing if the connection is closed and the Server's message
    public ReceivedMessage receiveMessage() {
        String responseLine = "";
        ReceivedMessage recvMsg = new ReceivedMessage();
        try {
            /// Retrieve server response
            if (inputReader.ready())
                responseLine = inputReader.readLine().trim();
            while (true) {
                /// Retrieve server response till END or FIN indicators
                if (responseLine.equalsIgnoreCase("END")) {
                    /// END indicating end of server message
                    recvMsg.isOpen = true;
                    return recvMsg;
                } else if (responseLine.equalsIgnoreCase("FIN")) {
                    /// FIN indicating server closing session
                    recvMsg.isOpen = false;
                    return recvMsg;
                }
                /// Actively retrieve incoming server messages
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
                /// Add delay to wait for new server messages
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

    // Sends a message to the server and receives a message back
    // 
    // # Arguments
    // 
    // \param msg Message to be sent to the server
    //
    // # Return Value
    //
    // \return ReceivedMessage Object containing if the connection is closed and the Server's message 
    public ReceivedMessage sendMessage(String msg) {
        /// Send client message to server, and retrieve server response
        outputStream.println(msg);
        ReceivedMessage recvMsg = this.receiveMessage();
        outputStream.flush();
        return recvMsg;
    }

    // Sends username and password to the Server for Login
    // 
    // # Arguments
    // 
    // \param username User's username for login verification
    // \param password User's password for login verification
    //
    // # Return Value
    //
    // \return String containing the Server's message, notifying if login was successful
    public String sendUsernamePassword(String username, String password) {
        /// Send username and password to server for client authentication
        this.sendMessage(username);
        ReceivedMessage recvMsg = this.sendMessage(password);
        this.numTries++;
        return recvMsg.msg;
    }

    // Gets number of login tries
    //
    // # Return Value
    //
    // \return int of the number of tries the user has made to login
    public int getNumTries() {
        return this.numTries;
    }

    // Gets receipt text from Server
    //
    // # Return Value
    //
    // \return String of receipt text from Server
    public String getInteractions() {
        /// Retrieve ATM receipt from server
        ReceivedMessage recvMsg = this.receiveMessage();
        return recvMsg.msg;
    }

    // Closes the connection between the Client and Server
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
        /// Retrieve commandline arguments
        ArgumentParser parser = ArgumentParsers.newFor("Client").build()
                .defaultHelp(true)
                .description("ATM Client frontend");
        parser.addArgument("-H", "--host")
                .type(String.class)
                .setDefault("127.0.0.1")
                .help("Specify which host to expose server on");
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
        
        Scanner scanner = new Scanner(System.in);
        Client client = new Client(ns.getString("host"), ns.getInt("port"));
        if (!client.startConnection())
            System.exit(0);
        /// Receive server prompt
        client.sendMessage(null);
        client.receiveMessage();

        /// Main server-client communication loop
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
