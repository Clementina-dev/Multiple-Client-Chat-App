package org.example.server;

import org.example.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
       try {
           while (!serverSocket.isClosed()) {
               Socket socket = serverSocket.accept();
               System.out.println("New Client Connected! 👩‍👨");

               ClientHandler clientHandler = new ClientHandler(socket);
               Thread thread = new Thread(clientHandler);
               thread.start();
           }
       } catch (IOException e) {
           closeServerSocket();
       }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serversocket = new ServerSocket(1234);
        Server server = new Server(serversocket);
        server.startServer();
    }
}
