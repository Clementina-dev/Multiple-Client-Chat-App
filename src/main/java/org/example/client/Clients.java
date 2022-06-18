package org.example.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Clients {
    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    String clientUserName;

    public Clients(Socket socket, String clientUserName) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = clientUserName;
        } catch (IOException e) {
            exitAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(clientUserName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bufferedWriter.write(clientUserName + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            exitAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receivingMessage() {
        new Thread(() -> {
            String userMessages;
                while (socket.isConnected()) {
                    try {
                        userMessages = bufferedReader.readLine();
                        System.out.println(userMessages);
                    } catch (IOException e) {
                        exitAll(socket, bufferedReader, bufferedWriter);
                    }
                }
        }).start();
    }

    public void exitAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome To Clemmy's Chat App");
        System.out.println("Chat and Connect with friends all over the world 😉!");
        System.out.print("Please enter your name to begin the chat: ");
        String clientUserName = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Clients clients = new Clients(socket, clientUserName);
        clients.receivingMessage();
        clients.sendMessage();
    }
}
