package javaServer;

import java.io.*;
import java.net.Socket;

public class HttpHandler {

    public static void handleRequest(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String requestLine = reader.readLine();
            System.out.println("Requisição recebida: " + requestLine);

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendErrorResponse(output, 400, "Bad Request");
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];
            if (path.equals("/")) {
                path = "/index.html";
            }

            switch (method) {
                case "GET":
                    handleGet(output, path);
                    break;
                case "POST":
                    handlePost(output, reader, path);
                    break;
                case "PUT":
                    handlePut(output, reader, path);
                    break;
                case "DELETE":
                    handleDelete(output, path);
                    break;
                default:
                    sendErrorResponse(output, 405, "Method Not Allowed");
                    break;
            }

            output.flush();
        } catch (IOException e) {
            System.err.println("Erro ao processar a requisição: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o socket do cliente: " + e.getMessage());
            }
        }
    }

    private static void handleGet(OutputStream output, String path) throws IOException {
        File file = new File("src/web" + path);
        if (file.exists() && !file.isDirectory()) {
            sendFileResponse(output, file);
        } else {
            sendErrorResponse(output, 404, "Not Found");
        }
    }

    private static void handlePost(OutputStream output, BufferedReader reader, String path) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestBody.append(line).append("\n");
        }

        System.out.println("Dados do POST recebidos: " + requestBody);

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: 22\r\n" +
                "\r\n" +
                "POST recebido com sucesso";
        output.write(response.getBytes());
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

    private static void handleDelete(OutputStream output, String path) throws IOException {
        File file = new File("src/web" + path);
        if (file.exists() && !file.isDirectory()) {
            if (file.delete()) {
                String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: 18\r\n" +
                        "\r\n" +
                        "Arquivo deletado";
                output.write(response.getBytes());
            } else {
                sendErrorResponse(output, 500, "Internal Server Error");
            }
        } else {
            sendErrorResponse(output, 404, "Not Found");
        }
    }

    private static void sendFileResponse(OutputStream output, File file) throws IOException {
        String responseHeader = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + getContentType(file) + "\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "\r\n";

        output.write(responseHeader.getBytes());

        FileInputStream fileInput = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInput.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        fileInput.close();
    }

    private static void sendErrorResponse(OutputStream output, int statusCode, String statusMessage) throws IOException {
        String errorResponse = String.format("""
            HTTP/1.1 %d %s\r\n
            Content-Type: text/plain; charset=UTF-8\r\n
            Content-Length: %d\r\n
            \r\n
            %s
            """, statusCode, statusMessage, statusMessage.length(), statusMessage);

        output.write(errorResponse.getBytes());
    }

    private static String getContentType(File file) {
        String fileName = file.getName();
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}
