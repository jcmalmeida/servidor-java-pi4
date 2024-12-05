package javaServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Semaphore;

public class User {
    private String name;
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private String proximoComunicado = null;

    private Semaphore mutEx = new Semaphore (1,true);

    public User (Socket socket, InputStream input, OutputStream output) throws Exception {
        if (socket == null)
            throw new Exception ("Conexao ausente");

        if (socket == null)
            throw new Exception ("Receptor ausente");

        if (output == null)
            throw new Exception ("Transmissor ausente");

        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void receive(String x) throws Exception {
        try {
            this.output.write(x.getBytes());
            this.output.flush();
        } catch (IOException erro) {
            throw new Exception ("Erro de transmissao");
        }
    }

    public String send() throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            if (this.proximoComunicado == null)
                this.proximoComunicado = reader.readLine();
            String ret         = this.proximoComunicado;
            this.proximoComunicado = null;
            return ret;
        } catch (Exception erro) {
            throw new Exception ("Erro de recepcao");
        }
    }

    public String sendJson() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        StringBuilder requestBody = new StringBuilder();
        while (reader.ready()) {
            requestBody.append((char) reader.read());
        }

        return requestBody.toString();
    }

    public void receiveFileResponse(File file) throws IOException {
        String responseHeader = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + getContentType(file) + "\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "\r\n";
        this.output.write(responseHeader.getBytes());

        FileInputStream fileInput = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInput.read(buffer)) != -1) {
            this.output.write(buffer, 0, bytesRead);
        }
        fileInput.close();
    }

    public void receiveErrorResponse(int statusCode, String statusMessage) throws IOException {
        String errorBody = "O recurso solicitado não foi encontrado.";
        String errorResponse = String.format("""
            HTTP/1.1 %d %s\r\n
            Content-Type: text/plain; charset=UTF-8\r\n
            Content-Length: %d\r\n
            Connection: close\r\n
            Date: %s\r\n
            \r\n
            %s
            """, statusCode, statusMessage, errorBody.length(), getCurrentDate(), errorBody);

        this.output.write(errorResponse.getBytes());
        try {
            this.goodbye();
        } catch (Exception error) {
            System.err.println("Erro: " + error.getMessage());
        }
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

    public void goodbye() throws Exception {
        try {
            this.output.close();
            this.input.close();
            this.socket.close();
        }
        catch (Exception erro) {
            throw new Exception ("Erro de desconexao");
        }
    }

    public void flush() throws Exception {
        try {
            this.output.flush();
        } catch (IOException erro) {
            throw new Exception ("Erro de transmissao");
        }
    }

    private static String getCurrentDate() {
        return java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
                .format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC));
    }
}
