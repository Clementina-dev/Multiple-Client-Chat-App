package org.example.models;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable{
    public List<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUserName + "has joined the chat! 🗣");

        } catch (IOException e) {
            exitAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void setClientHandlers(List<ClientHandler> clientHandlers) {
        this.clientHandlers = clientHandlers;
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

    public void broadcastMessage(String message) {
        for (ClientHandler handler : clientHandlers) {
            try {
                if (!handler.clientUserName.equals(clientUserName)) {
                    handler.bufferedWriter.write(message);
                    handler.bufferedWriter.newLine();
                    handler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                exitAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUserName + " has left the chat!");
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
