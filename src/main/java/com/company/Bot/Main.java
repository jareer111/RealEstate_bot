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
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        try {
            ComponentContainer.adminList.removeAll(Collections.singleton("5570937198"));
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            RealEstateBot myBot = new RealEstateBot();
            ComponentContainer.MY_BOT = myBot;

            botsApi.registerBot(myBot);

            sendMessageToAdmins();
        } catch (TelegramApiException e) {
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
