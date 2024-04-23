package by.alst.http;

import by.alst.service.ServerService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import static java.net.http.HttpRequest.BodyPublishers.ofFile;

public class HttpClientRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082"))
                .header("content-type", "application/json")
                .POST(ofFile(Path.of("src/main/resources/employeeList.json")))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Path htmlFilePath = Path.of("src/main/resources/clientSalary.html");
        ServerService.saveFile(htmlFilePath, response.body());
    }
}
