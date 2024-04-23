package by.alst.http;

import by.alst.database.EmployeeList;
import by.alst.service.ServerService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpServer {
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            var socket = serverSocket.accept();
            processSocket(socket);
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processSocket(Socket socket) {
        try (socket;
             var inputStream = new DataInputStream(socket.getInputStream());
             var outputStream = new DataOutputStream(socket.getOutputStream())) {

            byte[] bodyBytes = ServerService.getRequestJsonData(inputStream);

            String sourseHtmlPath = "src/main/resources/salary.html";
            String temporaryHtmlPath = "src/main/resources/salaryWorking.html";

            ServerService.makeTemporaryFile(sourseHtmlPath, temporaryHtmlPath);

            EmployeeList employeeList = ServerService.readFromJson(bodyBytes);
            ServerService.dataEnteringFile(temporaryHtmlPath, employeeList);

            byte[] body = Files.readAllBytes(Path.of(temporaryHtmlPath));
            outputStream.write("""
                    HTTP/1.1 200 OK
                    content-type: text/html
                    content-length: %s
                    """.formatted(body.length).getBytes());
            outputStream.write(System.lineSeparator().getBytes());
            outputStream.write(body);
            ServerService.deleteFile(temporaryHtmlPath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
