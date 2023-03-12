package com.company.files;

import com.company.container.ComponentContainer;
import com.company.dto.AdsDetailsDTO;
import com.company.entity.*;
import com.company.enums.State;
import com.company.util.InlineKeyboardUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.company.container.ComponentContainer.*;
import static com.company.controller.UserController.searchStatus;
import static com.company.controller.UserController.userStatus;

public class DbFunctionsImpl implements WorkWithDbFunctions {


    public static void getSearchByPrice(SearchPrice searchPrice, String chatId) {

        String query = BASE_QUERY + " (ads.price between ? AND ? ) AND   ads.district_id= ? AND ads.status_id=3 order by ads.updated_at; ";

        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, searchPrice.getPrice1());
            preparedStatement.setDouble(2, searchPrice.getPrice2());
            preparedStatement.setInt(3, searchPrice.getDistrictId());

            flagIsTrue(printResult(preparedStatement, chatId), chatId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void searchAdsByDate(String data, String chatId) {
        boolean flag = false;
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB)) {
            int date = Integer.parseInt(data);

            String query = BASE_QUERY + " ads.status_id=3 AND (ads.updated_at between  (NOW()::date - ?) AND now()); ";
            System.out.println(query);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, date);


            flagIsTrue(printResult(preparedStatement, chatId), chatId);
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getSearchResultsByNumberOfRooms(SearchHelper searchHelper, String chatId) {

        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
        ) {
            Class.forName("org.postgresql.Driver");
            String query = BASE_QUERY + "  ads.district_id=? AND ads.status_id=3 AND par.room_count=? order by ads.updated_at;";


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, searchHelper.getDistrictId());
            preparedStatement.setInt(2, searchHelper.getNumberOfRooms());

            flagIsTrue(printResult(preparedStatement, chatId), chatId);

            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getSearchByAdress(Ads ads, String chatId) {

        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
        ) {
            Class.forName("org.postgresql.Driver");
            String query = BASE_QUERY + " ads.sale_type_id=? AND ads.district_id=? AND ads.status_id=3 order by ads.updated_at ;";


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, ads.getSaleTypeId());
            preparedStatement.setInt(2, ads.getDistrictId());

            flagIsTrue(printResult(preparedStatement, chatId), chatId);

            preparedStatement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public static void getAdsById(int adsId) {
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
        ) {
            String sql = BASE_QUERY + "ads.id=?;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, adsId);

            flagIsTrue(printResult(preparedStatement, CHANEL_ID), CHANEL_ID);

            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getMyAdsHistory(String chatId) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);

            String query = BASE_QUERY + "u.chat_id = ? AND ads.status_id=3;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, chatId);

            flagIsTrue(printResult(preparedStatement, chatId), chatId);

            connection.close();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptAd(int adsId) {
        int i = 0;
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);) {
            Class.forName("org.postgresql.Driver");


            String statusChanger = " update ads set status_id = 3  where id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(statusChanger);
            preparedStatement.setInt(1, adsId);
            i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return i > 0;
    }

    @Override
    public boolean rejectAd(int adsId) {
        int i = 0;
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);) {
            Class.forName("org.postgresql.Driver");

            String statusChanger = " update ads set status_id = 2  where id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(statusChanger);
            preparedStatement.setInt(1, adsId);
            i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return i > 0;
    }

    @Override
    public int isAdsEmpty(String chatId) {
        int anInt = 0;
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            String query = """
                    select count(chat_id) from ads join users u on ads.user_id = u.id  where ? like '%'  ||chat_id||  '%';""";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, chatId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                anInt = resultSet.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return anInt;
    }

    @Override
    public Users getUserByChatId(String chatId) {
        Users users = new Users();
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB)) {
            Class.forName("org.postgresql.Driver");
            String query = """
                    select * from users where chat_id = ?;
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, chatId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.setId(resultSet.getInt("id"));
                users.setChatId(resultSet.getString("chat_id"));
                users.setFirstName(resultSet.getString("first_name"));
                users.setLastName(resultSet.getString("last_name"));
                users.setPhoneNumber(resultSet.getString("phone_number"));
                users.setUsername(resultSet.getString("username"));
                users.setActive(resultSet.getBoolean("is_active"));
                users.setBlocked(resultSet.getBoolean("is_blocked"));
                users.setAdmin(resultSet.getBoolean("is_admin"));
                System.out.println(users);
            }
            preparedStatement.close();
            return users;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean addAdvertisement(Ads ads) {

        try (Connection connection = DriverManager.getConnection(
                URL_DB, USER_DB, PASSWORD_DB);
        ) {

            String query = """
                    insert into ads(user_id,
                     ad_type, title, updated_at,
                    phone_number, sale_type_id,
                    district_id, parameter_id,
                     price, price_type_id, info,
                      home_type_id, status_id)
                    values (?,?,?,?,?,?,?,?,?,?,?,?,?);
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, (int) ads.getUserId());
            preparedStatement.setString(2, ads.getAdType());
            preparedStatement.setString(3, ads.getTitle());
            preparedStatement.setTimestamp(4, ads.getUpdatedAt());
            preparedStatement.setString(5, ads.getPhoneNumber());
            preparedStatement.setInt(6, ads.getSaleTypeId());
            preparedStatement.setInt(7, ads.getDistrictId());
            preparedStatement.setInt(8, ads.getParameterId());
            preparedStatement.setBigDecimal(9, ads.getPrice());
            preparedStatement.setInt(10, ads.getPriceTypeId());
            preparedStatement.setString(11, ads.getInfo());
            preparedStatement.setInt(12, ads.getHomeTypeId());
            preparedStatement.setInt(13, ads.getStatusId());

            int result = preparedStatement.executeUpdate();
            System.out.println("result = " + result);
            if (result == 1) return true;

            preparedStatement.close();
        } catch (Exception e) {
            return false;
        }


        return false;
    }

    @Override
    public List<Users> getAllUsers() {
        List<Users> users = new ArrayList<>();
        // by metod bazadagi barcha userlar ro'yxatini oladi va uni userlar listi ko'rinishida qaytaradi


        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = """
                    select * from users;
                    """;
            ResultSet resultSet = statement.executeQuery(query);
            int id;
            String chatId, firstname, lastName, username, phoneNumber;
            boolean isActive, isBlocked, isAdmin;
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                chatId = resultSet.getString(2);
                firstname = resultSet.getString(3);
                lastName = resultSet.getString(4);
                phoneNumber = resultSet.getString(5);
                username = resultSet.getString(6);
                isActive = resultSet.getBoolean(7);
                isBlocked = resultSet.getBoolean(8);
                isAdmin = resultSet.getBoolean(9);

                users.add(new Users(id, chatId, firstname, lastName, phoneNumber,
                        username, isActive, isBlocked, isAdmin));
            }

            connection.close();
            statement.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public List<String> getAdminsChatIds() {
        List<String> adminChatIDs = new ArrayList<>();
        // by metod bazadagi barcha userlar ro'yxatini oladi va uni userlar listi ko'rinishida qaytaradi


        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Statement statement = connection.createStatement();
            String query = """
                    select chat_id from users where is_admin = '1';
                    """;
            ResultSet resultSet = statement.executeQuery(query);

            String chatId;
            while (resultSet.next()) {

                chatId = resultSet.getString(1);
                adminChatIDs.add(chatId);
            }
            connection.close();
            statement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return adminChatIDs;
    }

    @Override
    public boolean blockUser(String chatId) {
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);

            String blockUser = " update users set is_blocked = ?" +
                    "where chat_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(blockUser);
            Users user = getUserByChatId(chatId);
            preparedStatement.setBoolean(1, !user.isBlocked());
            preparedStatement.setString(2, chatId);
            int i = preparedStatement.executeUpdate();
            connection.close();
            preparedStatement.close();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int setOrRemoveAdmin(String chatId) {

        // bu metodga chat id keladi va bu chat id ga ega userni admin qilib tayinlaydi
        // bu joyda agar user avval bloklangan bo'lsa uni avval is_blocked fildini
        // to'g'irlab admin etib belgilaydi.

        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            Users user = getUserByChatId(chatId);

            String isBlocked = " update users set is_blocked = ?, is_admin = ?, is_active = ?" +
                    "where chat_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(isBlocked);
            preparedStatement.setBoolean(1, false);
            preparedStatement.setBoolean(2, !user.isAdmin());
            preparedStatement.setBoolean(3, true);
            preparedStatement.setString(4, chatId);
            int i = preparedStatement.executeUpdate();
            if (i == 1 && !user.isAdmin()) {
                adminList.add(chatId);
                return 1;
            } else if (i == 1 && user.isAdmin()) {
                adminList.remove(chatId);
                return 2;
            }
            connection.close();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean removeAdmin(String chatId) {

        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);

            String isBlocked = " update users set is_blocked = ?, is_admin = ?, is_active = ?" +
                    "where chat_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(isBlocked);
            preparedStatement.setBoolean(1, false);
            preparedStatement.setBoolean(2, false);
            preparedStatement.setBoolean(3, true);
            preparedStatement.setString(4, chatId);
            int i = preparedStatement.executeUpdate();
            if (i == 1) {
                adminList.add(chatId);

                return true;
            }

            connection.close();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Users addUser(String chatId, Contact contact, String userName) {
        Users users = new Users();
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB)) {
            Class.forName("org.postgresql.Driver");
            String query = """
                    insert into users
                    (chat_id,first_name,last_name,phone_number,username,is_active)
                    values (?,?,?,?,?,?);
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, chatId);
            preparedStatement.setString(2, contact.getFirstName());
            preparedStatement.setString(3, contact.getLastName());
            preparedStatement.setString(4, contact.getPhoneNumber());
            preparedStatement.setString(5, userName);
            preparedStatement.setBoolean(6, true);

            int execute = preparedStatement.executeUpdate();

            connection.close();
            preparedStatement.close();

            System.out.println("execute = " + execute);
            System.out.println("Created!");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }


    @Override
    public void getAdsToCheck(String chatId) {  // todo admin
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            String query = BASE_QUERY + " ads.status_id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 1);


            flagIsTrue(printResult(preparedStatement, chatId), chatId);

            connection.close();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static District getDistrictById(String districtId) {
        District district = new District();

        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);) {
            Class.forName("org.postgresql.Driver");
            String query = "select * from district where id=" + districtId;

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                district.setId(resultSet.getInt(1));
                district.setRegionId(resultSet.getInt(2));
                district.setName(resultSet.getString(3));
            }

            statement.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return district;
    }

    public static Region getRegionById(String regionId) {
        Region region = new Region();

        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);) {
            Class.forName("org.postgresql.Driver");
            String query = "select * from region where id=" + regionId;

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                region.setId(resultSet.getInt(1));
                region.setName(resultSet.getString(2));
            }
            statement.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return region;
    }

    public static int addNewParameter(Parameter parameter) {
        Users users = new Users();
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);) {
            Class.forName("org.postgresql.Driver");
            String query = """
                    insert into parametr
                    (room_count,area,living_area,floor,max_floor,material_id, status_id)
                    values (?,?,?,?,?,?,?);
                    """;

            BigDecimal bigDecimal = new BigDecimal(parameter.getArea());
//            BigDecimal livingArea=new BigDecimal(String.valueOf(parameter.getLivingArea()));
            parameter.setLivingArea(BigDecimal.valueOf(0));
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, parameter.getRoomCount());
            preparedStatement.setBigDecimal(2, bigDecimal);
            preparedStatement.setBigDecimal(3, parameter.getLivingArea());
            preparedStatement.setInt(4, parameter.getFloor());
            if (parameter.getMaxFloor() == null) {
                parameter.setMaxFloor(1);
            }
            preparedStatement.setInt(5, parameter.getMaxFloor());
            preparedStatement.setInt(6, parameter.getMaterialId());
            preparedStatement.setInt(7, parameter.getStatusId());

            int execute = preparedStatement.executeUpdate();
            System.out.println("execute = " + execute);
            System.out.println("Created!");

            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static int getLastInsertParameterId() {

        int parameterId = 0;
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB)) {

            Class.forName("org.postgresql.Driver");
            String query = """
                    select id from parametr order by id desc;
                    """;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();

            parameterId = resultSet.getInt(1);

            connection.close();
            statement.close();

        } catch (Exception e) {
            WorkWithDbFunctions object = new DbFunctionsImpl();
            SendMessage sendMessage = new SendMessage();
            for (String adminsChatId : object.getAdminsChatIds()) {
                sendMessage.setChatId(adminsChatId);
                sendMessage.setText(String.valueOf(e));
                MY_BOT.sendMsg(sendMessage);
            }
        }
        return parameterId;
    }

    public static boolean addNewAds(Ads ads, String chatId, Parameter parameter) {
        DbFunctionsImpl db = new DbFunctionsImpl();
        Users user = db.getUserByChatId(chatId);
        int userId = user.getId();

        int execute = 0;


        Users users = new Users();
        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB)) {
            int addNewParameter = addNewParameter(parameter);
            if (addNewParameter == 0) throw new SQLException();

            Class.forName("org.postgresql.Driver");


            String query = """
                    insert into ads
                    (user_id,ad_type,phone_number,sale_type_id,district_id, 
                    parameter_id, price, price_type_id, info, home_type_id, photo_path,status_id)
                    values (?,?,?,?,?,?,?,?,?,?,?,?);
                    """;


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, String.valueOf(ads.getSaleTypeId()));
            preparedStatement.setString(3, ads.getPhoneNumber());
            preparedStatement.setInt(4, ads.getSaleTypeId());
            preparedStatement.setInt(5, ads.getDistrictId());
            int lastInsertParameterId = getLastInsertParameterId();
            preparedStatement.setInt(6, lastInsertParameterId);
            preparedStatement.setBigDecimal(7, ads.getPrice());
            preparedStatement.setInt(8, ads.getPriceTypeId());
            preparedStatement.setString(9, ads.getInfo());
            preparedStatement.setInt(10, ads.getHomeTypeId());
            String photo = ads.getPhotoPath();
            preparedStatement.setString(11, photo);
            preparedStatement.setInt(12, 1);

            execute = preparedStatement.executeUpdate();
            System.out.println("execute = " + execute);
            System.out.println("New ads added successfully!");

            connection.close();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return execute > 0;
    }

    public static HomeType getHomeTypeById(int homeId) {
        HomeType homeType = new HomeType();

        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);) {
            Class.forName("org.postgresql.Driver");
            String query = "select * from home_type where id=" + homeId;

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                homeType.setId(resultSet.getInt(1));
                homeType.setName(resultSet.getString(2));
            }
            connection.close();
            statement.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return homeType;
    }


    public static void searchAllAds(String chatId) {
        boolean flag = false;

        try (Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
        ) {
            Class.forName("org.postgresql.Driver");
            String query = """
                    select ads.photo_path, ht.name, ads.updated_at as "updated time",
                           ads.phone_number, st.name as "sale name", d.name as "district name",reg.name, par.room_count as "room count", par.area, par.floor,par.max_floor,m.name, ads.price "price",
                           cur.name "currency type",ads.info "description" from ads join users u
                                                                                         on ads.user_id = u.id join sale_type st on ads.sale_type_id = st.id join district d on ads.district_id = d.id join region reg on reg.id = d.region_id
                                                                                    join parametr par on  ads.parameter_id = par.id join currency cur on ads.price_type_id = cur.id join home_type ht on
                            ads.home_type_id=ht.id join ads_status stat on ads.status_id = stat.id join material m on par.material_id= m.id order by ads.updated_at ;""";

            PreparedStatement preparedStatement = connection.prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String photoPath = resultSet.getString(1);
                String str = "🏠 Bino turi:" + resultSet.getString(2) + "\n" +
                        "📲 Telefon: " + resultSet.getString(4) + "\n" +
                        "📄 E'lon turi: " + resultSet.getString(5) + "\n" +
                        "🏙 Viloyat: " + resultSet.getString(7) + "\n" +
                        "\uD83C\uDFD8 Tuman: " + resultSet.getString(6) + "\n" +
                        "🗺 Maydon: " + resultSet.getInt(9) + "\n" +
                        "\uD83D\uDD22 Xonalar soni: " + resultSet.getInt(8) + "\n";
                if (resultSet.getString(4).equals("Kvartira")) {
                    str += "🏢 Qavat: " + resultSet.getInt(10) + "/" + resultSet.getInt(11) + "\n";
                } else {
                    str += "🏢 Qavat: " + resultSet.getInt(11) + "\n";
                }
                str += "🏛️ Material: " + resultSet.getString(12) + "\n" +
                        "💰 Narxi: " + resultSet.getInt(13) + " ";
                if (resultSet.getString(14).equals("dollar")) {
                    str += "$";
                }
                str += "\n" +
                        "\uD83D\uDCAC  Qo'shimcha ma'lumot: " + resultSet.getString(15) + "\n"
                        + "\uD83D\uDCC5 Sana: " + resultSet.getString(3) + "\n";
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(photoPath));
                sendPhoto.setCaption("\n" + str);
                ComponentContainer.MY_BOT.sendMsg(sendPhoto);
                flag = true;
                System.out.println(str);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!flag) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Botga bironta ham e'lon joylanmagan qayta urinib kuring 🙅‍");
            MY_BOT.sendMsg(sendMessage);
        }
    }

    public static void editDistrictId(Ads ads, Integer newDistrictId) {
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);

            String query = " update ads  set district_id = ? where ads.id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newDistrictId);
            preparedStatement.setInt(2, ads.getId());

            int execute = preparedStatement.executeUpdate();
            System.out.println("execute = " + execute);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void editPrice(Ads ads, BigDecimal newPrice) {
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);

            String query = " update ads set price = ?" +
                    "where ads.id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBigDecimal(1, newPrice);
            preparedStatement.setInt(2, ads.getId());

            int execute = preparedStatement.executeUpdate();
            System.out.println("execute = " + execute);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void editNumberOfRooms(Ads ads, Integer newNumberOfRooms) {
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);

            String query = " update parametr p set p.room_count = ? from ads where p.id = ads.parameter_id and ads.id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newNumberOfRooms);
            preparedStatement.setInt(2, ads.getId());

            int execute = preparedStatement.executeUpdate();
            System.out.println("execute = " + execute);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void editPhotoUrl(Ads ads, String newPhotoUrl) {
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);

            String query = " update ads set photo_path = ?" +
                    "where ads.id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newPhotoUrl);
            preparedStatement.setInt(2, ads.getId());

            int execute = preparedStatement.executeUpdate();
            System.out.println("execute = " + execute);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void editInfo(Ads ads, String newInfo) {
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
            String query = " update ads set info = ?" +
                    "where ads.id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newInfo);
            preparedStatement.setInt(2, ads.getId());

            int execute = preparedStatement.executeUpdate();
            System.out.println("execute = " + execute);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    static boolean printResult(PreparedStatement preparedStatement, String chatId) {
        try {

            List<AdsDetailsDTO> adsDetailsDTOlist = getAdsDTOlist(preparedStatement);
            System.out.println("adsDetailsDTOlist.size() = " + adsDetailsDTOlist.size());
            buttonPressCount.put(chatId, 0);
            productMap.put(chatId, adsDetailsDTOlist);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            if (adsDetailsDTOlist.size() == 0) {
                sendMessage.setText("Sizning qidiruv natijalaringizga e'lon topilmadi qayta urinib koring 🙅‍");
                MY_BOT.sendMsg(sendMessage);
                userStatus.remove(chatId);
                searchStatus.remove(chatId);
            } else if (!adminList.contains(chatId) && !chatId.equals(CHANEL_ID)) {
                sendMessage.setText("Sizning qidiruv natijalaringiz 📰 ");
                MY_BOT.sendMsg(sendMessage);
                printAdsWithOrder(chatId, 0);
                searchStatus.put(chatId, State.SEARCHING_PROSSES);
            } else {
                printAdsWithOrder(chatId, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void printAdsWithOrder(String chatId, Integer road) {

        try {
            Integer msgid = nextprevorder.get(chatId);
            if (msgid != null) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(msgid);
                MY_BOT.sendMsg(deleteMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Integer order = buttonPressCount.get(chatId);
        int sum = order + road;
        AdsDetailsDTO dto = productMap.get(chatId).get(sum);
        buttonPressCount.put(chatId, sum);


        String photoPath = dto.getOne();
        String str = "🏠 Bino turi:" + dto.getTwo() + "\n" +
                "📲 Telefon: " + dto.getFour() + "\n" +
                "📄 E'lon turi: " + dto.getFive() + "\n" +
                "🏙 Viloyat: " + dto.getSeven() + "\n" +
                "🌃 Tuman: " + dto.getSix() + "\n" +
                "🗺 Maydon: " + dto.getNine() + "\n" +
                "\uD83D\uDD22 Xonalar soni: " + dto.getEight() + "\n";
        if (dto.getFour().equals("Kvartira")) {
            str += "🏢 Qavat: " + dto.getTen() + "/" + dto.getEleven() + "\n";
        } else {
            str += "🏢 Qavat: " + dto.getEleven() + "\n";
        }
        str += "🏛️ Material: " + dto.getTwelve() + "\n" +
                "💰 Narxi: " + dto.getThirteen() + "  " + dto.getFourteen() +
                "\n\n" +
                "\uD83D\uDCAC  Qo'shimcha ma'lumot: " + dto.getFifteen() + "\n"
                + "\uD83D\uDCC5  Sana: " + dto.getThree() + "\n\n" + "\uD83D\uDC49 https://t.me/uy_joy_uzbekistan";


        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(photoPath));
        sendPhoto.setCaption("\n" + str);

        EditMessageMedia emd = new EditMessageMedia();
        emd.setMedia(new InputMediaPhoto());
        if (adminList.contains(chatId)) {
            sendPhoto.setReplyMarkup(InlineKeyboardUtil.confirmAd(dto.getSixteen(), dto.getSeventeen()));  // todo admin yangi
        } else if (!chatId.equals(CHANEL_ID)) {
            sendPhoto.setReplyMarkup(InlineKeyboardUtil.nextOrPrev());
        }

        Message message = MY_BOT.sendMsg(sendPhoto);
        nextprevorder.put(chatId, message.getMessageId());

    }

    private static List<AdsDetailsDTO> getAdsDTOlist(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        List<AdsDetailsDTO> adsDetailsDTOlist = new ArrayList<>();
        int i = 0;
        while (resultSet.next()) {
            AdsDetailsDTO adsDetailsDTO = AdsDetailsDTO.builder()
                    .one(resultSet.getString(1))
                    .two(resultSet.getString(2))
                    .three(resultSet.getString(3))
                    .four(resultSet.getString(4))
                    .five(resultSet.getString(5))
                    .six(resultSet.getString(6))
                    .seven(resultSet.getString(7))
                    .eight(resultSet.getInt(8))
                    .nine(resultSet.getInt(9))
                    .ten(resultSet.getInt(10))
                    .eleven(resultSet.getInt(11))
                    .twelve(resultSet.getString(12))
                    .thirteen(resultSet.getInt(13))
                    .fourteen(resultSet.getString(14))
                    .fifteen(resultSet.getString(15))
                    .sixteen(resultSet.getInt(16))
                    .seventeen(resultSet.getString(17))
                    .build();
            adsDetailsDTOlist.add(adsDetailsDTO);
            i++;
        }
        return adsDetailsDTOlist;
    }


    public static void flagIsTrue(boolean flag, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!flag && !adminList.contains(chatId)) {
            sendMessage.setText("So'rovingizga mos e'lonlar topilmadi qayta urinib kuring ❌❌");
        } else if (!flag && adminList.contains(chatId)) {
            sendMessage.setText("Tasdiqlash uchun e'lonlar mavjud emas");
        }
        MY_BOT.sendMsg(sendMessage);

    }
}


