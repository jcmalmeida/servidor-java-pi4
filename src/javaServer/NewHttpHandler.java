package javaServer;

import java.io.*;
import java.net.Socket;

public class NewHttpHandler {

    public static void handleRequest(User user, String request) {
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
//                case "PUT":
//                    handlePut(output, reader, path);
//                    break;
//                case "DELETE":
//                    handleDelete(output, path);
//                    break;
                default:
                    user.receiveErrorResponse(405, "Method Not Allowed");
                    break;
            }
            user.flush();

        } catch (IOException e) {
            System.err.println("Erro ao processar a requisição: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        } finally {
//            try {
//                clientSocket.close();
//            } catch (IOException e) {
//                System.err.println("Erro ao fechar o socket do cliente: " + e.getMessage());
//            }
        }
    }

    private static void handleGet(User user, String request) throws IOException {
        System.out.println("Dados do GET recebidos: " + request);

        String requestLine = request.split("\n")[0];
        String[] requestParts = requestLine.split(" ");
        String path = requestParts[1];

        if (path.equals("/")) {
            path = "/index.html";
        } else if (path.equals("/campanha")) {
            path = "/campanha.html";
        }

        File file = new File("src/web" + path);
        if (file.exists() && !file.isDirectory()) {
            user.receiveFileResponse(file);
        } else {
            user.receiveErrorResponse(404, "Not Found");
        }
    }

    private static void handlePost(User user, String request) throws Exception {
        System.out.println("Dados do POST recebidos: " + request);
        String json = user.sendJson();
        System.out.println(json);

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: 22\r\n" +
                "\r\n" +
                "POST recebido com sucesso";
        System.out.println(response);
        user.receive(response);
    }

    private static void handlePut(OutputStream output, BufferedReader reader, String path) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestBody.append(line).append("\n");
        }

        System.out.println("Dados do PUT recebidos: " + requestBody);

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: 21\r\n" +
                "\r\n" +
                "PUT recebido com sucesso";
        output.write(response.getBytes());
    }

//    private static void handleDelete(OutputStream output, String path) throws IOException {
//        File file = new File("src/web" + path);
//        if (file.exists() && !file.isDirectory()) {
//            if (file.delete()) {
//                String response = "HTTP/1.1 200 OK\r\n" +
//                        "Content-Type: text/plain\r\n" +
//                        "Content-Length: 18\r\n" +
//                        "\r\n" +
//                        "Arquivo deletado";
//                output.write(response.getBytes());
//            } else {
//                sendErrorResponse(output, 500, "Internal Server Error");
//            }
//        } else {
//            sendErrorResponse(output, 404, "Not Found");
//        }
//    }
}
