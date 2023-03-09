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

    public void receiveMessage() {
        String responseLine = "";
        try {
            responseLine = inputReader.readLine();
            while (true) {
                if (responseLine.equalsIgnoreCase("END"))
                    break;
                System.out.println(responseLine);
                responseLine = inputReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        this.receiveMessage();
        outputStream.println(msg);
        outputStream.flush();
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
        client.sendMessage("\n"); // Receive server prompt
        while (true) {
            String input = scanner.nextLine();
            if (input != null && input.length() > 0)
                client.sendMessage(input);
            if (input.equalsIgnoreCase("terminate")) 
            {
                client.close();
                break;
            }
        }
        scanner.close();
    } 
}
