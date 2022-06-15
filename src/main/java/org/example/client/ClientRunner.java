package org.example.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientRunner implements Runnable{
    public static ArrayList<ClientRunner> clientRunners = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientRunner(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientRunners.add(this);
            broadcastMessage("SERVER: " + clientUserName + " has joined the chat! 🗣");

        } catch (IOException e) {
            exitAll(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {
        String clientMessaging;

        while (socket.isConnected()) {
            try {
                clientMessaging = bufferedReader.readLine();
                broadcastMessage(clientMessaging);
            } catch (IOException e) {
                exitAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientRunner clientRunner : clientRunners) {
            try {
                if (!clientRunner.clientUserName.equals(clientUserName)) {
                    clientRunner.bufferedWriter.write(messageToSend);
                    clientRunner.bufferedWriter.newLine();
                    clientRunner.bufferedWriter.flush();
                }
            } catch (IOException e) {
                exitAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientRunners.remove(this);
        broadcastMessage("SERVER: " + clientUserName + " has left the chat! 🚶‍♀");
    }

    public void exitAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
}
