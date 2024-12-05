package javaServer;

import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket socket;
    private ArrayList<User> users;

    public AceitadoraDeConexao (String porta, ArrayList<User> users) throws Exception {
        if (porta==null)
            throw new Exception ("Porta ausente");

        try {
            this.socket = new ServerSocket (Integer.parseInt(porta));
        } catch (Exception erro) {
            throw new Exception ("Porta invalida");
        }

        if (users==null)
            throw new Exception ("Usuarios ausentes");

        this.users = users;
    }

    public void run () {
        for(;;) {
            Socket conexao = null;

            try {
                conexao = this.socket.accept();
            } catch (Exception erro) {
                continue;
            }

            SupervisoraDeConexao supervisoraDeConexao = null;
            try {
                supervisoraDeConexao = new SupervisoraDeConexao (conexao, users);
            }
            catch (Exception erro)
            {} // sei que passei parametros corretos para o construtor
            supervisoraDeConexao.start();
        }
    }
}
