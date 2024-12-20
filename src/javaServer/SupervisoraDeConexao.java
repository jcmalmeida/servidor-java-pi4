package javaServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread {
    private User user;
    private Socket socket;
    private ArrayList<User> users;

    public SupervisoraDeConexao(Socket socket, ArrayList<User> users) throws Exception {
        if (socket == null)
            throw new Exception("Conexao ausente");

        if (users == null)
            throw new Exception("Usuarios ausentes");

        this.socket = socket;
        this.users = users;
    }

    public void run() {

        OutputStream output;
        try {
            output = this.socket.getOutputStream();
        } catch (Exception erro) {
            return;
        }

        InputStream input = null;
        try {
            input = this.socket.getInputStream();
        } catch (Exception erro) {
            try {
                output.close();
            } catch (Exception falha) {} // so tentando fechar antes de acabar a thread

            return;
        }

        try {
            this.user = new User(this.socket, input, output);
        } catch (Exception erro) {} // sei que passei os parametros corretos

        try {
            synchronized (this.users) {
                this.users.add(this.user);
            }

            for (;;) {
                String request = this.user.send();

                if (request == null)
                    return;

                String req = HttpHandler.handleRequest(this.user, request);

                if (req == "delete") {
                    synchronized (this.users) {
                        this.users.remove(this.user);
                    }
                    HtmlHandler.delete(user);
                    this.user.goodbye();
                }
            }
        } catch (Exception erro) {
            try {
                output.close();
                input.close();
            } catch (Exception falha) {} // so tentando fechar antes de acabar a thread

            return;
        }
    }
}
