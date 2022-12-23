package com.company.Bot;

import com.company.container.ComponentContainer;
import com.company.controller.AdminController;
import com.company.controller.UserController;
import com.company.db.Database;
import com.company.entity.Users;
import com.company.enums.AdminStatus;
import com.company.files.DbFunctionsImpl;
import com.company.files.WorkWithDbFunctions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class RealEstateBot extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        return ComponentContainer.BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return ComponentContainer.BOT_USERNAME;
    }
    static WorkWithDbFunctions object = new DbFunctionsImpl();
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();

            String chatId = String.valueOf(message.getChatId());
            Users users = object.getUserByChatId(chatId);

//            if (chatId.equals(ComponentContainer.ADMIN_CHAT_IDS)) {
            if (users.isAdmin()) {

                if(!ComponentContainer.adminStatusMap.containsKey(chatId))
                    ComponentContainer.adminStatusMap.put(chatId, AdminStatus.NOTHING);
                AdminController.handleMessage(user, message);
            } else {
                UserController.handleMessage(user, message);
            }

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            User user = callbackQuery.getFrom();
            String data = callbackQuery.getData();

            String chatId = String.valueOf(message.getChatId());
            Users users = object.getUserByChatId(chatId);
            if (users.isAdmin()) {
                if(!ComponentContainer.adminStatusMap.containsKey(chatId))
                    ComponentContainer.adminStatusMap.put(chatId, AdminStatus.NOTHING);
                AdminController.handleCallback(user, message,data);
            } else {
                UserController.handleCallback(user, message, data);
            }
        }
    }



    public Message sendMsg(Object obj) {
        try {
            if (obj instanceof SendMessage) {
                execute((SendMessage) obj);
            } else if (obj instanceof DeleteMessage) {
                execute((DeleteMessage) obj);
            } else if (obj instanceof EditMessageText) {
                execute((EditMessageText) obj);
            } else if (obj instanceof SendPhoto) {
                 execute((SendPhoto) obj);
            } else if (obj instanceof SendDocument) {
                execute((SendDocument) obj);
            }else if (obj instanceof ForwardMessage) {
                execute((ForwardMessage) obj);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
