package com.atm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintStream outputStream;
    private BufferedReader inputReader;

    public Client(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startConnection() {
        try {
            outputStream = new PrintStream(socket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean receiveMessage() {
        String responseLine = "";
        try {
            if (inputReader.ready()) responseLine = inputReader.readLine().trim();
            while (true) {
                if (responseLine.equalsIgnoreCase("END")) {
                    return true;
                } else if (responseLine.equalsIgnoreCase("FIN")) {
                    return false;
                }
                if (responseLine != "") System.out.println(responseLine);
                Thread.sleep(50);
                if (inputReader.ready()) responseLine = inputReader.readLine();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Boolean sendMessage(String msg) {
        outputStream.println(msg);
        Boolean isOpen = this.receiveMessage();
        outputStream.flush();
        return isOpen;
    }

    public void close() {
        try {
            inputReader.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client("127.0.0.1", 7777);
        client.startConnection();
        client.sendMessage(null); // Receive server prompt
        client.receiveMessage();
        Boolean isOpen = true;
        while (isOpen) {
            String input = scanner.nextLine();
            if (input != null && input.length() > 0)
                isOpen = client.sendMessage(input);
        }
        client.close();
        scanner.close();
    } 
}
