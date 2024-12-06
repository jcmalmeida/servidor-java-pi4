package javaServer;

import java.io.*;
import java.util.List;

import com.google.gson.Gson;
import javaServer.DAO.Campanhas;
import javaServer.DBO.Campanha;

public class HttpHandler {

    public static String handleRequest(User user, String request) {
        System.out.println("Current user: " + user.getName());
        String req = "";
        try {
            String requestLine = request.split("\n")[0];
            String[] requestParts = requestLine.split(" ");

            String method = requestParts[0];
            switch (method) {
                case "GET":
                    handleGet(user, request);
                    break;
                case "POST":
                    handlePost(user, request);
                    break;
                case "PUT":
                    handlePut(user, request);
                    break;
                case "DELETE":
                    req = "delete";
                    handleDelete(user, request);
                    break;
                default:
                    user.receiveErrorResponse(405, "Method Not Allowed");
                    break;
            }
            user.flush();

        } catch (IOException e) {
            System.err.println("Erro ao processar a requisição: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return req;
    }

    private static void handleGet(User user, String request) throws Exception {
        System.out.println("Dados do GET recebidos: " + request);

        String requestLine = request.split("\n")[0];
        String[] requestParts = requestLine.split(" ");
        String path = requestParts[1];

        if (path.equals("/"))
            path = "/index.html";
        else if (path.equals("/campanha")) {
            path = "/campanha.html";

            List<Campanha> campanhas = Campanhas.obter();
            Campanha campanhaAtiva = null;
            for (int i=0; i<campanhas.size(); i++) {
                if (campanhas.get(i).isDisparoContatoFeito()) {
                    campanhaAtiva = campanhas.get(i);
                }
            }

            String textoCampanha = getTextoCampanha(campanhaAtiva, user);
            String linkWhatsApp = getLinkWhatsApp(textoCampanha);

            boolean flag = false;
            while (!flag) {
                flag = HtmlHandler.modify(user, textoCampanha, linkWhatsApp);
            }

            path = "/campanha_" + user.getName() + ".html";
        }
        else if (path.equals("/blank"))
            path = "/blank.html";

        File file = new File("src/web" + path);
        if (file.exists() && !file.isDirectory()) {
            user.receiveFileResponse(file);
        } else {
            user.receiveErrorResponse(404, "Not Found");
        }
    }

    private static void handlePost(User user, String request) throws Exception {
        System.out.println("Dados do POST recebidos: " + request);

        String json = user.getJson();
        Gson gson = new Gson();
        MyName myName = gson.fromJson(json, MyName.class);
        user.setName(myName.name);

        String body = "POST recebido com sucesso";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n" +
                body;
        user.receive(response);
    }

    private static void handlePut(User user, String request) throws Exception {
        System.out.println("Dados do PUT recebidos: " + request);

        String json = user.getJson();
        Gson gson = new Gson();
        MyName myName = gson.fromJson(json, MyName.class);

        HtmlHandler.delete(user);

        user.setName(myName.name);

        String body = "PUT recebido com sucesso";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n" +
                body;
        user.receive(response);
    }

    private static void handleDelete(User user, String request) throws Exception {
        System.out.println("Dados do DELETE recebidos: " + request);

        String body = "Entidade deletada do servidor";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        user.receive(response);
    }

    private static class MyName {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    private static String getTextoCampanha(Campanha campanhaAtiva, User user) {
        if (campanhaAtiva == null)
            return "Olá! Aqui é a/o " + user.getName() + ". " +
                "Sei que você gosta muito de mim e seria meu doador de sangue caso eu precisasse. " +
                "Neste momento, outras pessoas precisam deste gesto. Eu vou contribuir, vamos juntos colaborar? " +
                "Acesse vidamais.com para saber como doar.";

        return "Olá! Aqui é a/o " + user.getName() + ". " +
                "Sei que você gosta muito de mim e seria meu doador de sangue caso eu precisasse.\n" +
                "Neste momento, outras pessoas precisam deste gesto. Eu vou contribuir, vamos juntos colaborar? " +
                "Seguem os dados: \n\n" +
                "Instituição para doação: " + (campanhaAtiva.getIdHemocentro() == 1 ? "Hemocentro da Unicamp" : "Hemocentro mais próximo") + "\n" +
                "Período de coleta: " + campanhaAtiva.getDataInicio() + " a " + campanhaAtiva.getDataFim() + ", das " +
                campanhaAtiva.getHorarioInicio() + " às " + campanhaAtiva.getHorarioFim() + "\n" +
                (campanhaAtiva.getIncentivo() == "" ? "" : ("Incentivo: " + campanhaAtiva.getIncentivo()));
    }

    private static String getLinkWhatsApp(String texto) {
        String baseUrl = "https://wa.me/?text=";
        return baseUrl + texto;
    }
}
