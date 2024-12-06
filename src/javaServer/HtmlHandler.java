package javaServer;

import java.io.*;
import java.nio.file.*;

public class HtmlHandler {

    public static boolean modify(User user, String texto, String link) {
        String filePath = "src/web/campanha_standard.html";
        String filePath2 = "src/web/campanha_" + user.getName() + ".html";
        String placeholder1 = "<!--PLACEHOLDER1-->";
        String placeholder2 = "<!--PLACEHOLDER2-->";
        String placeholder3 = "<!--PLACEHOLDER3-->";

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            content = content.replace(placeholder1, user.getName());
            content = content.replace(placeholder2, texto);
            content = content.replace(placeholder3, link);
            Files.write(Paths.get(filePath2), content.getBytes());
        } catch (IOException e) {
            System.err.println("Erro ao modificar o arquivo HTML: " + e.getMessage());
        }

        return true;
    }

    public static void delete(User user) {
        String filePath = "src/web/campanha_" + user.getName() + ".html";
        File file = new File(filePath);

        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (isDeleted) {
                System.out.println("Arquivo deletado com sucesso: " + filePath);
            } else {
                System.out.println("Não foi possível deletar o arquivo: " + filePath);
            }
        } else {
            System.out.println("Arquivo não encontrado: " + filePath);
        }
    }
}
