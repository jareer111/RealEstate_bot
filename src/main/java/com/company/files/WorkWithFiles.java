package com.company.files;

import com.company.entity.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.company.container.ComponentContainer.*;

public interface WorkWithFiles {

    Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public static void getUserFile(){

        File file = new File(BASE_FOLDER,"usersList.xlsx");
        List<Users> usersList = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = """
                    select * from users order by id;
                    """;

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String chatId = resultSet.getString("chat_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phoneNumber = resultSet.getString("phone_number");
                String username = resultSet.getString("username");
                boolean isActive = resultSet.getBoolean("is_active");
                boolean isBlocked = resultSet.getBoolean("is_blocked");
                boolean isAdmin = resultSet.getBoolean("is_admin");

//                System.out.println(id + chatId + firstName + lastName + phoneNumber + username + isActive + isBlocked + isAdmin);

                usersList.add(new Users(id, chatId, firstName, lastName, phoneNumber, username, isActive, isBlocked, isAdmin));
            }


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //////
//        File file = new File(BASE_FOLDER,"usersList.xlsx");

        try (FileOutputStream out = new FileOutputStream(file)) {

            XSSFWorkbook workbook = new XSSFWorkbook();

            XSSFSheet sheet = workbook.createSheet("Users");

            XSSFRow header = sheet.createRow(0);

            String[] columns = {"Id", "ChatId","FirstName","LastName", "PhoneNumber","Username", "IsActive", "IsBlocked",  "IsAdmin"};

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }


            for (int i = 0; i < usersList.size(); i++) {
                Users user = usersList.get(i);

                XSSFRow row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getChatId());
                row.createCell(2).setCellValue(user.getFirstName());
                row.createCell(3).setCellValue(user.getLastName());
                row.createCell(4).setCellValue(user.getPhoneNumber());
                row.createCell(5).setCellValue(user.getUsername());
                row.createCell(6).setCellValue(user.isActive());
                row.createCell(7).setCellValue(user.isBlocked());
                row.createCell(8).setCellValue(user.isAdmin());

            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
