package org.example.server;

import org.example.client.ClientRunner;

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

               ClientRunner clientRunner = new ClientRunner(socket);
               Thread thread = new Thread(clientRunner);
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
