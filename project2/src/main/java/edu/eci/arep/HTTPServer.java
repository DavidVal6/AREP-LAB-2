package edu.eci.arep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
import java.awt.*;

public class HTTPServer {

    public static File file;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {

            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean fline = true;
            boolean necessaryFlag = true;
            String uriS = "";
            String uriWithFileName = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (necessaryFlag) {
                    if (fline) {
                        fline = false;
                        uriS = uriS = inputLine.split(" ")[1];
                    }
                    if (inputLine.startsWith("Content-Disposition:")) {
                        necessaryFlag = false;
                        uriWithFileName = inputLine;
                    }
                }
                if (!in.ready()) {
                    break;
                }
            }
            if (uriS.startsWith("/upload")) {
                outputLine = findBoundaries(uriWithFileName);
            } else {
                outputLine = getHomeIndex();
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
    /**
     * This method will find 
     * @param inputString
     * @return
     */
    public static String findBoundaries(String inputString) {
        String[] parts = inputString.split(";");
        String filename = null;
        for (String part : parts) {
            if (part.trim().startsWith("filename")) {
                // Extraer el nombre del archivo de la parte 'filename'
                String[] filenameParts = part.split("=");
                if (filenameParts.length > 1) {
                    filename = filenameParts[1].trim().replace("\"", "");
                }
            }
        }
        String path = "project2\\\\target\\\\resource\\\\";
        System.out.println("Filename: " + "/" + filename);
        return getTheArchive(filename, path);
    }

    public static String getTheArchive(String filename, String path) {
        String completePath = path + filename;
        file = new File(completePath);
        int extensionIndex = filename.lastIndexOf(".");
        String type = extensionIndex != -1 ? filename.substring(extensionIndex + 1) : null;
        File test = new File("project2\\target\\resource\\home.html");
        System.out.println(test);
        System.out.println(test.exists());
        if (file.exists()) {
            System.out.println("Existe");
            try{
                switch (type) {
                case "html":
                    return toHTML(file);
                case "txt":
                    return toHTML(file);
                case "js":
                    return toJs(file);

                case "css":
                    return toCSS(file);

                case "jpg":
                    return toImage(file,type);
                case "jpge":
                    return toImage(file,type);
                
                case "jpeg":
                    return toImage(test, type);

                case "png":
                    return toImage(test, type);
            }
            }catch(IOException e){
                e.printStackTrace();
            }
            
        } else {
            System.out.println("NO existe");
        }
        return "Si";
    }

    public static String toImage(File file, String type) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/"+ type + "\r\n"
                + "\r\n"
                + "<img src=\"data:image/" + type + ";base64," + base64 + "\">";
    }

    public static String toHTML(File file) throws IOException {
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + body;
    }

    public static String toCSS(File file) throws IOException{
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/\r\n"
                + "\r\n"
                + body;
    }

    public static String toJs(File file)throws IOException{
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/javascript\r\n"
                + "\r\n"
                + body;
    }

    public static StringBuilder fromArchiveToString(File file) throws IOException{
        StringBuilder body = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line).append("\n");
        }
        reader.close();
        return body;
    }

    public static String getHomeIndex() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>File Upload</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background-color: #f0f0ff;\n" +
                "            font-family: \"Ubuntu\", sans-serif;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            text-align: center;\n" +
                "            margin-top: 50px;\n" +
                "        }\n" +
                "\n" +
                "        label, input[type=\"file\"], input[type=\"button\"] {\n" +
                "            display: block;\n" +
                "            margin: 0 auto;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>UPLOAD A FILE</h1>\n" +
                "    <form action=\"/upload\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
                "        <label for=\"file\">Choose a file:</label><br>\n" +
                "        <input type=\"file\" id=\"file\" name=\"file\"><br><br>\n" +
                "        <input type=\"button\" value=\"Upload\" onclick=\"uploadFile()\">\n" +
                "    </form>\n" +
                "\n" +
                "    <div id=\"uploadMsg\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "        function uploadFile() {\n" +
                "            const fileInput = document.getElementById(\"file\");\n" +
                "            const file = fileInput.files[0];\n" +
                "            const formData = new FormData();\n" +
                "            formData.append(\"file\", file);\n" +
                "\n" +
                "            const xhr = new XMLHttpRequest();\n" +
                "            xhr.onload = function () {\n" +
                "                document.getElementById(\"uploadMsg\").innerHTML = this.responseText;\n" +
                "            };\n" +
                "            xhr.open(\"POST\", \"/upload\");\n" +
                "            xhr.send(formData);\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>\n";
    }

}
