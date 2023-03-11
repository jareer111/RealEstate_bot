package com.company.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.company.container.ComponentContainer.*;

public class InlineKeyboardUtil {
    private static List<InlineKeyboardButton> getRow(InlineKeyboardButton... buttons) {
        return new ArrayList<>(List.of(buttons));
    }

    public static InlineKeyboardMarkup getCancel() {
        InlineKeyboardButton button = new InlineKeyboardButton(InlineButtonConstants.CANCEL);
        button.setCallbackData(InlineButtonConstants.CANCEL_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }


    public static InlineKeyboardMarkup getConnectMarkup(String chatId, Integer messageId) {
        InlineKeyboardButton button = new InlineKeyboardButton(InlineButtonConstants.REPLY_DEMO);
        button.setCallbackData(InlineButtonConstants.REPLY_CALL_BACK + "/" + chatId + "/" + messageId);

        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }

    public static ReplyKeyboard getSaveTestButtons() {
        InlineKeyboardButton button = new InlineKeyboardButton(InlineButtonConstants.ADD);
        button.setCallbackData(InlineButtonConstants.ADD_CALL_BACK);
        InlineKeyboardButton button1 = new InlineKeyboardButton(InlineButtonConstants.CANCEL);
        button1.setCallbackData(InlineButtonConstants.CANCEL_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(List.of(button, button1)));
    }

    public static InlineKeyboardMarkup getSaleTypeId() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();
        try {
            Connection connection = null;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = """
                    select * from sale_type;
                    """;

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                BASE_SALE_TYPE.put(resultSet.getInt(1), resultSet.getString(2));
                List<InlineKeyboardButton> listOfButtons = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                button.setText(resultSet.getString(2));
                listOfButtons.add(button);
                generalList.add(listOfButtons);
            }
            connection.close();
            statement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        markup.setKeyboard(generalList);
        return markup;
    }

    public static InlineKeyboardMarkup confirmAd(int adsId, String userId) {
        int adsID =adsId+100;
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> general = new ArrayList<>();
//        List<List<InlineKeyboardButton>> general2 = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button.setCallbackData(InlineButtonConstants.CONFIRM_AD_CALL_BACK + "/" + adsId + "/" + userId + "/" + adsID);//+ "/" + chatId+"/"+messageId);
        button.setText(InlineButtonConstants.CONFIRM_AD);
        button1.setCallbackData(InlineButtonConstants.REJECT_AD_CALL_BACK + "/" + adsId + "/" + userId + "/" + adsID);//+ "/" + chatId+"/"+messageId);
        button1.setText(InlineButtonConstants.REJECT_AD);
        button3.setCallbackData(InlineButtonConstants.PREV_AD_CALL_BACK);
        button3.setText(InlineButtonConstants.PREV_AD);
        button4.setCallbackData(InlineButtonConstants.NEXT_AD_CALL_BACK);
        button4.setText(InlineButtonConstants.NEXT_AD);
        general.add(new ArrayList<>(List.of(button1, button)));
        general.add(new ArrayList<>(List.of(button3, button4)));
        markup.setKeyboard(general);
//        markup.setKeyboard(general2);
        return markup;

    }


    public static ReplyKeyboard nextOrPrev() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> general = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardButton button1 = new InlineKeyboardButton();

        button.setCallbackData(InlineButtonConstants.PREV_AD_CALL_BACK);
        button.setText(InlineButtonConstants.PREV_AD);
        button1.setCallbackData(InlineButtonConstants.NEXT_AD_CALL_BACK);
        button1.setText(InlineButtonConstants.NEXT_AD);
        general.add(new ArrayList<>(List.of(button, button1)));
        markup.setKeyboard(general);
        return markup;
    }


    public static InlineKeyboardMarkup getRegions() {  //todo
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();
        try {
            Connection connection = null;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = """
                    select * from region;
                    """;

            ResultSet resultSet = statement.executeQuery(query);

            int i = 0;
            List<InlineKeyboardButton> listOfButtons = new ArrayList<>();
            ;
            while (resultSet.next()) {
                if (i % 2 == 1) {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                    button.setText(resultSet.getString(2));
                    listOfButtons.add(button);
                } else {
                    listOfButtons = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                    button.setText(resultSet.getString(2));
                    listOfButtons.add(button);
                    generalList.add(listOfButtons);
                }
                i++;
            }
            connection.close();
            statement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        markup.setKeyboard(generalList);

        return markup;
    }

    public static ReplyKeyboard getDistrictByRegionId(String data) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();
        try {
            Connection connection = null;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = "select * from district where region_id=" + data + ";";

            ResultSet resultSet = statement.executeQuery(query);

            int i = 0;
            List<InlineKeyboardButton> listOfButtons = new ArrayList<>();
            ;
            while (resultSet.next()) {
                if (i % 2 == 1) {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                    button.setText(resultSet.getString(3));
                    listOfButtons.add(button);
                } else {
                    listOfButtons = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                    button.setText(resultSet.getString(3));
                    listOfButtons.add(button);
                    generalList.add(listOfButtons);
                }
                i++;
            }
            connection.close();
            statement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        markup.setKeyboard(generalList);

        return markup;
    }

    public static ReplyKeyboard getMaterial() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();

        try {
            Connection connection = null;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = "select * from material;";

            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                BASE_MATERIAL_HOME.put(resultSet.getInt(1), resultSet.getString(2));
                List<InlineKeyboardButton> listOfButtons = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                button.setText(resultSet.getString(2));
                listOfButtons.add(button);
                generalList.add(listOfButtons);
            }
            connection.close();
            statement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        markup.setKeyboard(generalList);
        return markup;
    }

    public static ReplyKeyboard getHomeTypes() {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();

        try {
            Connection connection = null;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = "select * from home_type;";

            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                List<InlineKeyboardButton> listOfButtons = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                button.setText(resultSet.getString(2));
                listOfButtons.add(button);
                generalList.add(listOfButtons);
            }
            connection.close();
            statement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        markup.setKeyboard(generalList);
        return markup;
    }

    public static ReplyKeyboard getCurrencies() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();

        try {
            Connection connection = null;
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = "select * from currency;";

            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                BASE_PRICE_NAME.put(resultSet.getInt(1), resultSet.getString(2));
                List<InlineKeyboardButton> listOfButtons = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData(String.valueOf(resultSet.getInt(1)));
                button.setText(resultSet.getString(2));
                listOfButtons.add(button);
                generalList.add(listOfButtons);
            }
            connection.close();
            statement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        markup.setKeyboard(generalList);
        return markup;


    }

    public static ReplyKeyboard confirmOrDelete() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();


        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData("confirm");
        button.setText("Tasdiqlash✅");
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData("delete");
        button1.setText("O'chirish\uD83D\uDDD1");
        generalList.add(new ArrayList<>(List.of(button, button1)));
        markup.setKeyboard(generalList);
        return markup;
    }

    public static ReplyKeyboard getSearchInlineMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData("region");
        button.setCallbackData("Hudud");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setCallbackData("price");
        button2.setCallbackData("Narx");
        generalList.add(new ArrayList<>(List.of(button, button2)));

        markup.setKeyboard(generalList);
        return markup;
    }

    public static ReplyKeyboard getSubSearchMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();


        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setCallbackData("forSale");
        button3.setCallbackData("Sotiladigan");

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setCallbackData("forRent");
        button4.setCallbackData("Ijaraga beriluvchi");
        generalList.add(new ArrayList<>(List.of(button3, button4)));


        markup.setKeyboard(generalList);
        return markup;
    }

    public static ReplyKeyboard editMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();

        InlineKeyboardButton phone = new InlineKeyboardButton();
        phone.setCallbackData("editPhone");
        phone.setText("Telefon raqam");

        InlineKeyboardButton district = new InlineKeyboardButton();
        district.setCallbackData("editDistrict");
        district.setText("Manzil");

        generalList.add(new ArrayList<>(List.of(phone, district)));

        InlineKeyboardButton price = new InlineKeyboardButton();
        price.setCallbackData("editPrice");
        price.setText("Narxi");

        InlineKeyboardButton roomCount = new InlineKeyboardButton();
        roomCount.setCallbackData("editRoomCount");
        roomCount.setText("Manzil");

        generalList.add(new ArrayList<>(List.of(price, roomCount)));

        InlineKeyboardButton info = new InlineKeyboardButton();
        info.setCallbackData("editInfo");
        info.setText("Ma'lumot");

        InlineKeyboardButton photo = new InlineKeyboardButton();
        photo.setCallbackData("editPhotoUrl");
        photo.setText("Rasim");
        generalList.add(new ArrayList<>(List.of(info, photo)));

        markup.setKeyboard(generalList);
        return markup;
    }

    public static ReplyKeyboard getSearchDateAds() {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();


        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData("7");
        button.setText("1️⃣➖HAFTALIK");
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData("30");
        button1.setText("1️⃣➖OYLIK");
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setCallbackData("90");
        button2.setText("3️⃣➖OYLIK");
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setCallbackData("300");
        button3.setText("HAMMASI 💯 ");

        generalList.add(new ArrayList<>(List.of(button, button1)));
        generalList.add(new ArrayList<>(List.of(button2, button3)));
        markup.setKeyboard(generalList);
        return markup;
    }

    public static ReplyKeyboard getEditMenu(int adsId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> generalList = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(adsId + "/editAd");
        button.setText("Tahrirlash");
        generalList.add(new ArrayList<>(List.of(button)));

        markup.setKeyboard(generalList);
        return markup;
    }

}
