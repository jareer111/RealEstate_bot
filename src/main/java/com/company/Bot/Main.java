package com.company.Bot;


import com.company.container.ComponentContainer;
import com.company.entity.Ads;
import com.company.entity.Users;
import com.company.files.DbFunctionsImpl;
import com.company.files.WorkWithDbFunctions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        try {
            DbFunctionsImpl dbFunctions = new DbFunctionsImpl();
            List<Users> users = dbFunctions.getAllUsers();
            users.stream().filter(Users::isAdmin).forEach(user -> ComponentContainer.adminList.add(user.getChatId()));
            System.out.println("admins chat id" + ComponentContainer.adminList);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            RealEstateBot myBot = new RealEstateBot();
            ComponentContainer.MY_BOT = myBot;

            botsApi.registerBot(myBot);

            sendMessageToAdmins();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendMessageToAdmins() {
        WorkWithDbFunctions object = new DbFunctionsImpl();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Bot ishga tushdi.");

        for (String chatId : object.getAdminsChatIds()) {
            sendMessage.setChatId(chatId);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }
    }
}
