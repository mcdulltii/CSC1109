package com.atm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
// import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class ReceivedMessage {
    public Boolean isOpen = false;
    public String msg = "";
}

public class Client {
    private Socket socket;
    private PrintStream outputStream;
    private BufferedReader inputReader;
    private Boolean isCLI;
    private int numTries;

    public Client(String ip, int port)  {
        this.numTries = 0;
        this.isCLI = true;
        try {
            this.socket = new Socket(ip, port);
        } catch (UnknownHostException e) {
            outputStream.println("Please check host address input.");
        } catch (IllegalArgumentException e) {
            outputStream.println("Port parameter is out of range.");
        } catch (IOException e) {
            outputStream.println("Unable to create socket.");
        } 
    }

    public Client(String ip, int port, Boolean isCLI) {
        this.numTries = 0;
        this.isCLI = isCLI;
        try {
            this.socket = new Socket(ip, port);
        } catch (UnknownHostException e) {
            outputStream.println("Please check host address input.");
        } catch (IllegalArgumentException e) {
            outputStream.println("Port parameter is out of range.");
        } catch (IOException e) {
            outputStream.println("Unable to create socket.");
        } 
    }

    public void startConnection() {
        try {
            outputStream = new PrintStream(socket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            outputStream.println("Unable to connect. ");
        }
    }

    public ReceivedMessage receiveMessage() {
        String responseLine = "";
        ReceivedMessage recvMsg = new ReceivedMessage();
        try {
            if (inputReader.ready())
                responseLine = inputReader.readLine().trim();
            while (true) {
                if (responseLine.equalsIgnoreCase("END")) {
                    recvMsg.isOpen = true;
                    return recvMsg;
                } else if (responseLine.equalsIgnoreCase("FIN")) {
                    recvMsg.isOpen = false;
                    return recvMsg;
                }
                if (responseLine != "") {
                    if (responseLine.length() > 3  &&
                        responseLine.substring(responseLine.length() - 3).equalsIgnoreCase("END")) {
                        String responseString = responseLine.substring(0, responseLine.length() - 3);
                        if (this.isCLI)
                            System.out.print(responseString);
                        recvMsg.isOpen = true;
                        recvMsg.msg += responseString;
                        return recvMsg;
                    } else {
                        if (this.isCLI)
                            System.out.println(responseLine);
                        recvMsg.msg += responseLine;
                    }
                }
                Thread.sleep(50);
                if (inputReader.ready())
                    responseLine = inputReader.readLine();
            }
        } catch (IOException | InterruptedException e) {
            outputStream.println("Unable to read stream.");;
        }
        recvMsg.isOpen = true;
        return recvMsg;
    }

    public ReceivedMessage sendMessage(String msg) {
        outputStream.println(msg);
        ReceivedMessage recvMsg = this.receiveMessage();
        outputStream.flush();
        return recvMsg;
    }

    public String sendUsernamePassword(String username, String password) {
        this.sendMessage(username);
        ReceivedMessage recvMsg = this.sendMessage(password);
        this.numTries++;
        return recvMsg.msg;
    }

    public int getNumTries() {
        return this.numTries;
    }

    public void close() {
        try {
            inputReader.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            outputStream.println("Unable to close socket.");
        }
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client("127.0.0.1", 7777);
        client.startConnection();
        client.sendMessage(null); // Receive server prompt
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
