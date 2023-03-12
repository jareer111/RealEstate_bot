package com.company.controller;


import com.company.entity.*;
import com.company.enums.State;
import com.company.files.DbFunctionsImpl;
import com.company.files.WorkWithDbFunctions;
import com.company.util.InlineButtonConstants;
import com.company.util.InlineKeyboardUtil;
import com.company.util.KeyboardButtonConstants;
import com.company.util.KeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;

import java.math.BigDecimal;
import java.util.*;

import static com.company.container.ComponentContainer.*;

public class UserController {
    public static Map<String, State> userStatus = new HashMap<>();
    public static Map<String, State> searchStatus = new HashMap<>();
    static Map<String, Ads> userAds = new HashMap<>();

    // javohir
    static Map<String, Ads> searchAds = new HashMap<>();
    static Map<String, Region> searchRegion = new HashMap<>();


    static Map<String, String> forRegionId = new HashMap<>();

    static Map<String, Parameter> userAdParameters = new HashMap<>();

    static WorkWithDbFunctions object = new DbFunctionsImpl();

    static Map<String, SearchHelper> userSearch = new HashMap<>();
    static Map<String, SearchPrice> searchPriceMap = new HashMap<>();
    public final static String messageFloor = """
            Uy nechanchi qavatda joylashgani va bino umumiy necha
            qavatdan iborat ekanligini Misol: \"<b>2/4</b>\" ko'rinishda yuboring
            """;

    public static void handleMessage(User user, Message message) {

        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        } else if (message.hasContact()) {
            Contact contact = message.getContact();
            handleContact(user, message, contact);
        } else if (message.hasPhoto()) {
            List<PhotoSize> photoSizeList = message.getPhoto();
            handlePhoto(user, message, photoSizeList);
        }
    }

    private static void handleContact(User user, Message message, Contact contact) {

        String chatId = String.valueOf(message.getChatId());
        Users customer = object.getUserByChatId(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (customer.getId() == 0) {
            customer = object.addUser(chatId, contact, user.getUserName());
            sendMessage.setText("Azoligingiz tasdiqlandi" +
                    "\nBotdan maroqli foydalaning");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());

        } else {
            sendMessage.setText("Menu: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
        }
        MY_BOT.sendMsg(sendMessage);
    }

    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        Users customer = object.getUserByChatId(chatId);
        Ads s_ads = searchAds.get(chatId);

        State state1 = userStatus.get(chatId);

        if (text.equals("/start")) {
            if (customer.getId() == 0) {
                sendMessage.setText("Assalomu alaykum!");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getContactMenu());
                MY_BOT.sendMsg(sendMessage);
            } else {
                if (customer.isBlocked()) {
                    sendMessage.setText("Accountingiz Blocklangan");
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.BlockedUserMenu());
                    MY_BOT.sendMsg(sendMessage);
                } else {
                    sendMessage.setText("Menu");
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                    MY_BOT.sendMsg(sendMessage);
                }
            }

        } else if (text.equals(KeyboardButtonConstants.CONTACT_WITH_ADMIN)) {

            customerMap.put(chatId, true);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Adminga murojat yuboring \uD83D\uDCAC ");
            MY_BOT.sendMsg(sendMessage);
        } else if (customerMap.containsKey(chatId)) {

            customerMap.remove(chatId);

            sendMessage.setText("Xabaringiz adminga jo'natildi! 😊");
            MY_BOT.sendMsg(sendMessage);

            String str = "ChatId : " + customer.getChatId() + "\n Full name: " + customer.getFirstName() +
                    "\nPhone number: " + customer.getPhoneNumber() +
                    "\nText: " + text;
            SendMessage sendMessage1 = new SendMessage();
            for (String adminChatId : object.getAdminsChatIds()) {

                sendMessage1.setText(str);
                sendMessage1.setChatId(adminChatId);
                sendMessage1.setReplyMarkup(InlineKeyboardUtil.getConnectMarkup(chatId, message.getMessageId()));
                MY_BOT.sendMsg(sendMessage1);
                break;
            }
        } else if (customer.isBlocked()) {
            sendMessage.setText("Accountingiz Blocklangan");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.BlockedUserMenu());
            MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.MY_ADS)) {
            int adsEmpty = object.isAdsEmpty(chatId);
            if (adsEmpty > 0) {
                object.getMyAdsHistory(chatId);
            } else {
                sendMessage.setText("Sizda e'lonlar yo'q");
                MY_BOT.sendMsg(sendMessage);
            }
        } else if (text.equals(KeyboardButtonConstants.SEND_AD_TO_ADMIN)) {
            sendMessage.setText("E'lon berish uchun quyidagi malumotlarni kiring  ✍️");

            userAds.put(chatId, new Ads());
            MY_BOT.sendMsg(sendMessage);

            userStatus.put(chatId, State.GET_HOME_TYPE_ID);

            sendMessage.setText("Bino turini tanlang: ");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getHomeTypes());
            MY_BOT.sendMsg(sendMessage);

        }

// javohir
        else if (text.equals(KeyboardButtonConstants.BACK_MENU)) {

            sendMessage.setText("Asosiy menu");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
            MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.SEARCH)) {

            sendMessage.setText("Qidirish menyusi");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getSearchMenu());
            MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.S_BY_ADDRES)) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            sendMessage.setText("Nima maqsadda qidiryapsiz ");
            searchAds.put(chatId, new Ads());
            searchRegion.put(chatId, new Region());
            userStatus.put(chatId, State.SEARCH_GET_SALE_TYPE_ID);
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getSaleTypeId());
            MY_BOT.sendMsg(sendMessage);

        } else if (text.equals(KeyboardButtonConstants.S_BY_COUNT_HOME)) {
            userSearch.put(chatId, new SearchHelper());

            sendMessage.setText(" Xonalar sonini kiriting");
            userStatus.put(chatId, State.S_ENTER_COUNT_ROOM);
            MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.S_BY_PRICE)) {

            searchPriceMap.put(chatId, new SearchPrice());
            sendMessage.setText("Narx bo'yicha qidirish uchun quyida \n" +
                    "ko'rsatilganidek narx oralig'ini yuboring:\n" +
                    "200-300;");
            MY_BOT.sendMsg(sendMessage);
            userStatus.put(chatId, State.GET_PRICE);
        } else if (text.equals(KeyboardButtonConstants.S_BY_HOME_DATE)) {
            sendMessage.setText("Oxirgi nechchi kunlik elonlarni kurmoqchisiz 📄 ");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getSearchDateAds());
            userStatus.put(chatId, State.S_DATE);
            MY_BOT.sendMsg(sendMessage);

        } else if (state1.equals(State.S_ENTER_COUNT_ROOM)) {
            try {
                int count = Integer.parseInt(text);
                if (count < 1) {
                    sendMessage.setText("Xonalar soni xato kiritildi qayta urining.");
                    MY_BOT.sendMsg(sendMessage);
                } else {
                    SearchHelper searchHelper = userSearch.get(chatId);
                    searchHelper.setNumberOfRooms(count);
                    sendMessage.setText("Xonalar soni qabul qilindi ");
                    MY_BOT.sendMsg(sendMessage);
                    sendMessage.setText("Qaysi viloyatdan izlayapsiz?");
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getRegions());
                    MY_BOT.sendMsg(sendMessage);
                    userStatus.put(chatId, State.SEARCH_REGION);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage.setText("Xonalar soni xato kiritildi. Qayta urinib ko'ring.");
                MY_BOT.sendMsg(sendMessage);
            }
        } else {
            State state = userStatus.get(chatId);
            Ads ads = userAds.get(chatId);
            if (state.equals(State.GET_PHONE_NUMBER)) {
                if (isValidNumber(text)) {
                    ads.setPhoneNumber(text);
                    userStatus.put(chatId, State.GET_SALE_TYPE_ID);
                    sendMessage.setText("Telefon raqam qabul qilindi");
                    MY_BOT.sendMsg(sendMessage);

                    sendMessage.setText("E'lon berishingizdan maqsadni tanlang: ");
                    sendMessage.setChatId(chatId);
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getSaleTypeId());

                    MY_BOT.sendMsg(sendMessage);

                } else {
                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(chatId);
                    deleteMessage.setMessageId(message.getMessageId());
                    MY_BOT.sendMsg(deleteMessage);
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Noto'g'ri turdagi raqam kiritildi.\n" +
                            "Raqamni qaytadan yuboring.");
                    Message msg = MY_BOT.sendMsg(sendMessage);
                }
            } else if (state.equals(State.ROOM_COUNT)) {

                int numberOfRooms = Integer.parseInt(text);

                userAdParameters.put(chatId, new Parameter());

                sendMessage.setChatId(chatId);

                if (numberOfRooms < 1 || numberOfRooms > 100) {
                    sendMessage.setText("Noto'g'ri son kiritildi. Xonalar sonini qaytadan kiriting: ");
                } else {
                    Parameter parameter = userAdParameters.get(chatId);
                    parameter.setRoomCount(numberOfRooms);
                    sendMessage.setText("Xonalar soni qabul qilindi.\n" +
                            "Uyning maydonini kiriting(m kv): ");
                    userStatus.put(chatId, State.AREA);
                }
                MY_BOT.sendMsg(sendMessage);
            } else if (state.equals(State.AREA)) {
                int area = Integer.parseInt(text);
                sendMessage.setChatId(chatId);
                if (area < 0) {
                    sendMessage.setText("Uyning maydoni xato kiritildi.\n" +
                            "Qaytadan kiriting: ");
                } else {
                    userStatus.put(chatId, State.FLOOR);
                    Parameter parameter = userAdParameters.get(chatId);
                    parameter.setArea(area);

                    if (ads.getHomeTypeId() == 1) {
                        sendMessage.setText(messageFloor);
                        sendMessage.setParseMode(ParseMode.HTML);
                    } else {
                        sendMessage.setText("Bino necha qavatdan iborat:");
                    }
                }
                MY_BOT.sendMsg(sendMessage);
            } else if (state.equals(State.FLOOR)) {
                if (ads.getHomeTypeId() == 1) {
                    String[] split = text.split("/");

                    try {
                        int floor = Integer.parseInt(split[0]);
                        int maxFloor = Integer.parseInt(split[1]);

                        sendMessage.setChatId(chatId);

                        if (floor < 0 || maxFloor < 0 || floor > maxFloor) {
                            sendMessage.setText(" Malumot xato kiritildi." + messageFloor);
                            sendMessage.setParseMode(ParseMode.HTML);
                        } else {
                            Parameter parameter = userAdParameters.get(chatId);
                            parameter.setFloor(floor);
                            parameter.setMaxFloor(maxFloor);
                            sendMessage.setText("Ma'lumot qabul qilindi.\n" +
                                    "Uyning materiali qanday?");
                            sendMessage.setReplyMarkup(InlineKeyboardUtil.getMaterial());
                            userStatus.put(chatId, State.MATERIAL_ID);

                        }
                        MY_BOT.sendMsg(sendMessage);

                    } catch (Exception e) {
//                        sendMessage.setChatId("912429653");
                        sendMessage.setText(String.valueOf(e));
                        MY_BOT.sendMsg(sendMessage);
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("Ma'lumot xato kiritildi." + messageFloor);
                        sendMessage.setParseMode(ParseMode.HTML);
                    }
                } else {
                    sendMessage.setChatId(chatId);
                    try {
                        int floor = Integer.parseInt(text);
                        Parameter parameter = userAdParameters.get(chatId);
                        parameter.setFloor(floor);
                        sendMessage.setText("Qavatlar soni qabul qilindi.\n" +
                                "Uyning materiali qanday?");
                        userStatus.put(chatId, State.MATERIAL_ID);
                        sendMessage.setReplyMarkup(InlineKeyboardUtil.getMaterial());
                    } catch (Exception e) {
                        sendMessage.setText("Qavatlar soni xato kiritildi.\nQaytadan kiriting: ");
                    }
                    MY_BOT.sendMsg(sendMessage);
                }
            } else if (state.equals(State.PRICE)) {
                sendMessage.setChatId(chatId);
                try {

                    double price = Double.parseDouble(text);
                    if (price < 0) {
                        sendMessage.setText("Narx xato kiritildi. Qaytadan kiriting:");
                    } else {
                        ads.setPrice(new BigDecimal(price));
                        sendMessage.setText("Narx qabul qilindi");
                        MY_BOT.sendMsg(sendMessage);
                        sendMessage.setText("Xonalar sonini kiriting: ");
                        userStatus.put(chatId, State.ROOM_COUNT);
                        MY_BOT.sendMsg(sendMessage);
                    }

                } catch (Exception e) {
                    sendMessage.setText("Xato narx kiritildi. Qaytadan urinib ko'ring.");
                }
            } else if (state.equals(State.INFO)) {


                Parameter parameter = userAdParameters.get(chatId);
                ads.setInfo(text);

                userStatus.put(chatId, State.GET_PHOTO);
                sendMessage.setText("Ma'lumot qabul qilindi.\n" +
                        "Bino (uy)ning rasmini yuboring: ");
                sendMessage.setChatId(chatId);
                MY_BOT.sendMsg(sendMessage);
            } else if (state.equals(State.GET_PRICE)) {
                try {
                    String[] split = text.split("-");
                    int price1 = Integer.parseInt(split[0]);
                    int price2 = Integer.parseInt(split[1]);
                    if (price1 < 0 || price2 < 0 || price1 > price2) {
                        sendMessage.setText("Narx xato kiritildi qayta urining.");
                        MY_BOT.sendMsg(sendMessage);
                    } else {
                        SearchPrice searchPrice = searchPriceMap.get(chatId);
                        searchPrice.setPrice1(price1);
                        searchPrice.setPrice2(price2);
                        sendMessage.setText("Narx oralig'i qabul qilindi ");
                        MY_BOT.sendMsg(sendMessage);
                        sendMessage.setText("Qaysi viloyatdan izlayapsiz?");
                        sendMessage.setReplyMarkup(InlineKeyboardUtil.getRegions());
                        MY_BOT.sendMsg(sendMessage);
                        userStatus.put(chatId, State.SEARCH_REGION_BY_PRICE);  // todo
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage.setText("Narx xato kiritildi. Qayta urinib ko'ring.");
                    MY_BOT.sendMsg(sendMessage);
                }
            } else {
                sendMessage.setText("Notug'ri amal bajarildi qayta urinib kuring ♻️");
                MY_BOT.sendMsg(sendMessage);
            }
        }
    }


    public static boolean isValidNumber(String number) {
        return number.matches("[+]998[0-9]{9}");
    }


    public static void handleCallback(User user, Message message, String data) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        State state = userStatus.get(chatId);
        Ads ads = userAds.get(chatId);
        Ads s_ads = searchAds.get(chatId);
        Region s_region = searchRegion.get(chatId);


        DeleteMessage deleteMessage = new DeleteMessage();

        if (data.endsWith("/editAd")) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);
            String[] split = data.split("/");
            int adsId = Integer.parseInt(split[0]);
        }

        if (state.equals(State.GET_SALE_TYPE_ID)) {
            userStatus.put(chatId, State.GET_REGION_ID);

            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            sendMessage.setText("Type tanlandi");
            MY_BOT.sendMsg(sendMessage);

            ads.setSaleTypeId(Integer.parseInt(data));
            sendMessage.setChatId(chatId);
            sendMessage.setText("Manzilni aniqlash uchun " +
                    "viloyatni tanlang: ");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getRegions());
            MY_BOT.sendMsg(sendMessage);

        } else if (state.equals(State.GET_REGION_ID)) {
            forRegionId.put(chatId, data);
            userStatus.put(chatId, State.GET_DISTRICT_ID);

            sendMessage.setText("Viloyat tanlandi.");
            MY_BOT.sendMsg(sendMessage);
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            sendMessage.setChatId(chatId);
            sendMessage.setText("Tumanni tanlang: ");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getDistrictByRegionId(data));
            MY_BOT.sendMsg(sendMessage);

        } else if (state.equals(State.GET_DISTRICT_ID)) {

            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            ads.setDistrictId(Integer.parseInt(data));
            District district = DbFunctionsImpl.getDistrictById(data);
            sendMessage.setChatId(chatId);

            Region region = DbFunctionsImpl.getRegionById(String.valueOf(district.getRegionId()));

            sendMessage.setText(region.getName() + ", " + district.getName() + " tumani tanlandi.");
            MY_BOT.sendMsg(sendMessage);

            userStatus.put(chatId, State.PRICE_TYPE_ID);
            sendMessage.setText("Narxni qaysi valyutada kiritmoqchisiz?");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCurrencies());
            MY_BOT.sendMsg(sendMessage);

        } else if (state.equals(State.PRICE_TYPE_ID)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            sendMessage.setText("Valyuta turi tanlandi.");
            MY_BOT.sendMsg(sendMessage);

            userStatus.put(chatId, State.PRICE);
            ads.setPriceTypeId(Integer.parseInt(data));

            sendMessage.setText("Narxni kiriting: ");
            MY_BOT.sendMsg(sendMessage);
        } else if (state.equals(State.GET_HOME_TYPE_ID)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            ads.setHomeTypeId(Integer.parseInt(data));

            sendMessage.setText("Bino turi tanlandi.");
            MY_BOT.sendMsg(sendMessage);

            userStatus.put(chatId, State.GET_PHONE_NUMBER);
            sendMessage.setText("Telefon raqamni yuboring.\n" +
                    "Misol: +998991234567");
            sendMessage.setChatId(chatId);

            MY_BOT.sendMsg(sendMessage);

        } else if (state.equals(State.MATERIAL_ID)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            Parameter parameter = userAdParameters.get(chatId);
            parameter.setMaterialId(Integer.parseInt(data));

            userStatus.put(chatId, State.INFO);
            sendMessage.setText("Qo'shimcha ma'lumotingizni kiriting: ");
            sendMessage.setChatId(chatId);
            MY_BOT.sendMsg(sendMessage);
        }

        // Javohir


        else if (state.equals(State.SEARCH_GET_SALE_TYPE_ID)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            s_ads.setSaleTypeId(Integer.parseInt(data));
            sendMessage.setChatId(chatId);
            sendMessage.setText("Type tanlandi.\n" +
                    "Viloyatni tanlang:");
            userStatus.put(chatId, State.SEARCH_GET_REGION_ID);
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getRegions());
            MY_BOT.sendMsg(sendMessage);

        } else if (state.equals(State.SEARCH_GET_REGION_ID)) {

            userStatus.put(chatId, State.SEARCH_GET_DISTRICT_ID);


            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            s_region.setId(Integer.parseInt(data));

            sendMessage.setChatId(chatId);
            sendMessage.setText("Tumanni tanlang: ");


            sendMessage.setReplyMarkup(InlineKeyboardUtil.getDistrictByRegionId(data));
            MY_BOT.sendMsg(sendMessage);


        } else if (state.equals(State.SEARCH_GET_DISTRICT_ID)) {

            userStatus.put(chatId, State.SEARCH_GET_MENU);

            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            s_ads.setDistrictId(Integer.parseInt(data));
//            s_ads.setUserId(Long.parseLong(chatId));
            DbFunctionsImpl db = new DbFunctionsImpl();
            db.getSearchByAdress(s_ads, chatId);
            sendMessage.setChatId(chatId);
            MY_BOT.sendMsg(sendMessage);

        } else if (state.equals(State.SEARCH_REGION)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            SearchHelper searchHelper = userSearch.get(chatId);
            searchHelper.setRegionId(Integer.valueOf(data));
            sendMessage.setText("Viloyat tanlandi");
            MY_BOT.sendMsg(sendMessage);
            sendMessage.setText("Tumanni tanlang: ");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getDistrictByRegionId(data));
            MY_BOT.sendMsg(sendMessage);
            userStatus.put(chatId, State.SEARCH_DISTRICT);

        } else if (state.equals(State.SEARCH_DISTRICT)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            SearchHelper searchHelper = userSearch.get(chatId);
            searchHelper.setDistrictId(Integer.valueOf(data));

            DbFunctionsImpl.getSearchResultsByNumberOfRooms(searchHelper, chatId);
            userSearch.remove(chatId);
        } else if (state.equals(State.SEARCH_REGION_BY_PRICE)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            SearchPrice searchPrice = searchPriceMap.get(chatId);
            searchPrice.setRegionId(Integer.parseInt(data));
            sendMessage.setText("Viloyat tanlandi");
            MY_BOT.sendMsg(sendMessage);
            sendMessage.setText("Tumanni tanlang ");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getDistrictByRegionId(data));
            MY_BOT.sendMsg(sendMessage);
            userStatus.put(chatId, State.SEARCH_DISTRICT_BY_PRICE);

        } else if (state.equals(State.SEARCH_DISTRICT_BY_PRICE)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());

            MY_BOT.sendMsg(deleteMessage);
            SearchPrice searchPrice = searchPriceMap.get(chatId);
            searchPrice.setDistrictId(Integer.parseInt(data));
            DbFunctionsImpl.getSearchByPrice(searchPrice, chatId);
            searchPriceMap.remove(chatId);
        } else if (state.equals(State.S_DATE)) {
            if (data.equals(InlineButtonConstants.NEXT_AD_CALL_BACK)) {
                DbFunctionsImpl.printAdsWithOrder(chatId, 1);
            } else if (data.equals(InlineButtonConstants.PREV_AD_CALL_BACK)) {
                DbFunctionsImpl.printAdsWithOrder(chatId, -1);
            } else {
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(message.getMessageId());
                MY_BOT.sendMsg(deleteMessage);
                DbFunctionsImpl.searchAdsByDate(data, chatId);
                userStatus.remove(chatId);
            }
        } else if (state.equals(State.CONFIRMATION_STATE)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);
            if (data.equals("confirm")) {
                Ads ads1 = userAds.get(chatId);
                Parameter parameter = userAdParameters.get(chatId);

                DbFunctionsImpl.addNewAds(ads1, chatId, parameter);

                sendMessage.setText("Adminga e'lon yuborildi✅" +
                        "\nE'lonni admin tasdiqlagandan so'ng qidiruv bo'limidan topishingiz mumkin bo'ladi❗️");

            } else if (data.equals("delete")) {
                userStatus.remove(chatId);
                sendMessage.setText("E'lon bekor qilindi");
            }
            MY_BOT.sendMsg(sendMessage);
        } else {
            sendMessage.setText("Notug'ri amal tanlandi qayta urinib kuring ♻️");
            MY_BOT.sendMsg(sendMessage);
//            DeleteMessage deleteMessage1 = new DeleteMessage();
//            deleteMessage1.setMessageId(message.getMessageId());
//            deleteMessage1.setChatId(chatId);
//            MY_BOT.sendMsg(deleteMessage1);
        }

    }

    private static void handlePhoto(User user, Message message, List<PhotoSize> photoSizeList) {
        String chatId = String.valueOf(message.getChatId());

        String fileId = photoSizeList.get(photoSizeList.size() - 1).getFileId();

        State state = userStatus.get(chatId);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);

        if (state.equals(State.GET_PHOTO)) {
            Ads ads = userAds.get(chatId);

            if (ads.getInfo() == null) {
                ads.setInfo("E'lon");
            }

            Parameter parameter = userAdParameters.get(chatId);

            ads.setPhotoPath(fileId);
            userStatus.put(chatId, State.CONFIRMATION_STATE);
            sendPhoto.setPhoto(new InputFile(ads.getPhotoPath()));

            String caption = "🏠 Bino turi: " + DbFunctionsImpl.getHomeTypeById(ads.getHomeTypeId()).getName() + "\n" +
                    "📲 Telefon: " + ads.getPhoneNumber() + "\n" +
                    "📄 E'lon turi: " + BASE_SALE_TYPE.get(ads.getSaleTypeId()) + "\n" +
                    "\uD83D\uDCCD Viloyat: " + DbFunctionsImpl.getRegionById(String.valueOf(DbFunctionsImpl.getDistrictById(String.valueOf(ads.getDistrictId())).getRegionId())).getName() + "\n" +
                    "\uD83C\uDFD5 Tuman: " + DbFunctionsImpl.getDistrictById(String.valueOf(ads.getDistrictId())).getName() + "\n" +
                    "🗺 Maydon: " + parameter.getArea() + " m kv \n" +
                    "\uD83D\uDD22 Xonalar soni: " + parameter.getRoomCount() + "\n";
            if (parameter.getMaxFloor() != null) {
                caption += "🏢 Qavat: " + parameter.getFloor() + "/" + parameter.getMaxFloor() + "\n";
            } else {
                caption += "🏢 Qavat: " + parameter.getFloor() + "\n";
            }
            caption += "🏛️ Material: " + BASE_MATERIAL_HOME.get(parameter.getMaterialId()) + "\n" +
                    "💰 Narxi: " + ads.getPrice() + " " + BASE_PRICE_NAME.get(ads.getPriceTypeId()) + "\n\n" +
                    "\uD83D\uDCAC  Qo'shimcha ma'lumot: \n" + ads.getInfo() + "\n\n\n" +
                    "E'lonni tasdiqlaysizmi  \uD83D\uDCCC ";

            sendPhoto.setCaption(caption);
            sendPhoto.setParseMode(ParseMode.HTML);
            sendPhoto.setReplyMarkup(InlineKeyboardUtil.confirmOrDelete());

            MY_BOT.sendMsg(sendPhoto);


        }

    }
}

