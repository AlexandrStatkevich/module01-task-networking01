package by.alst.service;

import by.alst.database.Employee;
import by.alst.database.EmployeeList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ServerService {

    @SuppressWarnings("deprecation")
    public static byte[] getRequestJsonData(DataInputStream dataInputStream)
            throws IOException {

        int contentLength = 0;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (!(line = dataInputStream.readLine()).isEmpty()) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
            if (line.startsWith("Content-Length:")) {
                String value = line.substring("Content-Length:".length()).trim();
                contentLength = Integer.parseInt(value);
            }
        }
        byte[] bodyBytes = dataInputStream.readNBytes(contentLength);

        String requestTxtFilePath = "src/main/resources/requestEmployee.txt";
        try (FileOutputStream fos = new FileOutputStream(requestTxtFilePath)) {
            fos.write(stringBuilder.toString().getBytes());
            fos.write(bodyBytes);
            System.out.println("Request was saved in " + new File(requestTxtFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodyBytes;
    }

    public static EmployeeList readFromJson(byte[] bodyBytes) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return bodyBytes.length != 0
                ? objectMapper.readValue(bodyBytes, EmployeeList.class)
                : new EmployeeList();
    }

    public static void dataEnteringFile(String filePath, EmployeeList employeeList) {
        try {
            String temp = Files.readString(Path.of(filePath));

            temp = temp.replace("${total_income}", String.valueOf(getTotalIncome(employeeList)));
            temp = temp.replace("${total_tax}", String.valueOf(getTotalTax(employeeList)));
            temp = temp.replace("${total_profit}", String.valueOf(getTotalProfit(employeeList)));

            Files.write(Path.of(filePath), temp.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeTemporaryFile(String sourseFilePath, String workingFilePath)
            throws IOException {
        try (FileInputStream is = new FileInputStream(sourseFilePath);
             FileOutputStream os = new FileOutputStream(workingFilePath)) {
            os.write(is.readAllBytes());
        }
    }

    public static void saveFile(Path filePath, String fileContent) {
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.write(filePath, fileContent.getBytes(), StandardOpenOption.WRITE);
            System.out.println(filePath + " was saved successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String path) {
        File deleteFile = new File(path);
        if (deleteFile.exists()) {
            if (deleteFile.delete()) {
                System.out.println("Temporary file " + deleteFile + " deleted successfully");
            } else {
                System.out.println("Failed to delete the file");
            }
        } else {
            System.out.println("File does not exist");
        }
    }

    public static Long getTotalIncome(EmployeeList employeeList) {
        return employeeList.getEmployees().stream().mapToLong(Employee::getSalary).sum();
    }

    public static Long getTotalTax(EmployeeList employeeList) {
        return employeeList.getEmployees().stream().mapToLong(Employee::getTax).sum();
    }

    public static Long getTotalProfit(EmployeeList employeeList) {
        return getTotalIncome(employeeList) - getTotalTax(employeeList);
    }
}
