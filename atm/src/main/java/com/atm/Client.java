package com.atm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;

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
            out = new PrintStream(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        out.println(msg + "\n");
        try {
            String line = in.readLine();
            while(line != null && line.length() > 0)
            {
                System.out.println(line);
                line = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.flush();
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client("127.0.0.1", 7777);
        client.startConnection();
        client.sendMessage(""); //Grabs server prompt
        while (true) {
            String input = scanner.nextLine();
            if (input != null && input.length() > 0)
                client.sendMessage(input);
            if (input.equalsIgnoreCase("terminate")) 
                client.close();
        }
    } 
}
