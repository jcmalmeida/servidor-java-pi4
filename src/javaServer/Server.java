package javaServer;

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {
        int DEFAULT_PORT = 8080;

        try {
            ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Servidor web iniciado na porta " + DEFAULT_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> HttpHandler.handleRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}
