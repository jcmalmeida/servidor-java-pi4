package javaServer;

import java.io.*;
import com.google.gson.Gson;

public class NewHttpHandler {

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

    private static void handleGet(User user, String request) throws IOException {
        System.out.println("Dados do GET recebidos: " + request);

        String requestLine = request.split("\n")[0];
        String[] requestParts = requestLine.split(" ");
        String path = requestParts[1];

        if (path.equals("/"))
            path = "/index.html";
        else if (path.equals("/campanha"))
            path = "/campanha.html";
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
}
