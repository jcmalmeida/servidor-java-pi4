package javaServer;

import java.io.*;
import java.nio.file.*;

public class HtmlModifier {

    public static void main(String[] args) {
        String filePath = "src/index.html";
        String placeholder = "<!--PLACEHOLDER-->";
        String newText = "<p>Texto inserido dinamicamente.</p>";

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            content = content.replace(placeholder, newText);
            Files.write(Paths.get(filePath), content.getBytes());
            System.out.println("Texto inserido com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao modificar o arquivo HTML: " + e.getMessage());
        }
    }
}
