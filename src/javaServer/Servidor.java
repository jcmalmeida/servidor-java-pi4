package javaServer;

import java.util.*;

public class Servidor {
    public static String DEFAULT_PORT = "3000";

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso esperado: java Servidor [PORTA]\n");
            return;
        }

        String porta = Servidor.DEFAULT_PORT;

        if (args.length == 1)
            porta = args[0];

        ArrayList<User> users = new ArrayList<User>();
        AceitadoraDeConexao aceitadoraDeConexao = null;
        try {
            aceitadoraDeConexao = new AceitadoraDeConexao(porta, users);
            aceitadoraDeConexao.start();
        } catch (Exception erro) {
            System.err.println("Escolha uma porta apropriada e liberada para uso!\n");
            return;
        }

        for (;;) {
            System.out.println("O servidor esta ativo! Para desativa-lo, use o comando \"desativar\"\n");
            System.out.print("> ");

            String request = null;
            try {
                request = Teclado.getUmString();
            } catch (Exception erro) {}

//            if (request.toLowerCase().equals("desativar"))
//            {
//                synchronized (usuarios)
//                {
//                    ComunicadoDeDesligamento comunicadoDeDesligamento =
//                            new ComunicadoDeDesligamento ();
//
//                    for (Parceiro usuario:usuarios)
//                    {
//                        try
//                        {
//                            usuario.receba (comunicadoDeDesligamento);
//                            usuario.adeus  ();
//                        }
//                        catch (Exception erro)
//                        {}
//                    }
//                }
//
//                System.out.println ("O servidor foi desativado!\n");
//                System.exit(0);
//            }
//            else
//                System.err.println ("Comando invalido!\n");
        }
    }
}

