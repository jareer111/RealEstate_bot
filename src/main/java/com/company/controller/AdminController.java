package com.company.controller;

import com.company.container.*;
import com.company.entity.*;
import com.company.enums.AdminStatus;
import com.company.files.DbFunctionsImpl;
import com.company.files.WorkWithDbFunctions;
import com.company.files.WorkWithFiles;
import com.company.util.InlineButtonConstants;
import com.company.util.KeyboardButtonConstants;
import com.company.util.KeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.File;

import static com.company.container.ComponentContainer.*;


public class AdminController {


    static WorkWithDbFunctions object = new DbFunctionsImpl();
    static String blockingChatId = null;

    public static void handleMessage(User user, Message message) {

        String s = String.valueOf(message.getChatId());

        if (adminStatusMap.get(s).equals(AdminStatus.AD_SEND_1)) {
            for (Users oneUser : object.getAllUsers()) {
                if (!oneUser.isAdmin()) {
                    ForwardMessage forwardMessage = new ForwardMessage(oneUser.getChatId(),
                            String.valueOf(message.getChatId()), message.getMessageId());
                    ComponentContainer.MY_BOT.sendMsg(forwardMessage);
                }
            }

            adminStatusMap.put(s, AdminStatus.NOTHING);

        } else if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        } else if (message.hasContact()) {
            Contact contact = message.getContact();
            handleContact(user, message, contact);
        }


    }


    private static void handleContact(User user, Message message, Contact contact) {
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.setText("Menu: ");
        sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
        ComponentContainer.MY_BOT.sendMsg(sendMessage);

    }


    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (text.equals("/start")) {
            adminStatusMap.put(chatId, AdminStatus.NOTHING);

            sendMessage.setText("Hello " + user.getFirstName());

            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        } else if (text.equalsIgnoreCase(KeyboardButtonConstants.SET_ADMIN)) {
            sendMessage.setText("Adminning chat idsini kiriting: ");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            adminStatusMap.put(chatId, AdminStatus.SETTING_ADMIN);

        } else if (text.equalsIgnoreCase(KeyboardButtonConstants.CONFIRM_ADD_FROM_USER)) {
            object.getAdsToCheck(chatId);

        } else if (text.equalsIgnoreCase(KeyboardButtonConstants.BLOCK_USER)) {
            sendMessage.setText("Foydalanuvchining chat idsini kiriting: ");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            adminStatusMap.put(chatId, AdminStatus.BLOCKING_P1);
        } else if (ComponentContainer.adminAnswerMap.containsKey(chatId)) {

            MessageData messageData = ComponentContainer.adminAnswerMap.get(chatId);

            Integer messageId = messageData.getMessage().getMessageId();
            String messageText = messageData.getMessage().getText();
            String customerChatId = messageData.getCustomerChatId();


            sendMessage.setChatId(customerChatId);
            sendMessage.setText("Adminning sizga javobi: \uD83D\uDCE8 \n "+text);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            String str = "AdminId : " + chatId +
                    "\nFull name: " + messageData.getMessage().getForwardSenderName() +
                    "\nText: " + messageText +
                    "\nUshbu xabarga javob berildi \n---------------------------" +
                    "\nJavob bergan Admin: " + "@" + user.getUserName() +
                    "\n Javob bergan Admin ismi: " + user.getFirstName() +
                    "\nJavob Matni: " + text;
            editMessageText.setText(str);
            editMessageText.setMessageId(messageId);
            ComponentContainer.MY_BOT.sendMsg(editMessageText);
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            ComponentContainer.adminAnswerMap.remove(chatId);


        }
        else if (adminStatusMap.get(chatId).equals(AdminStatus.SETTING_ADMIN)) {
            int isAdmin = object.setOrRemoveAdmin(text);
            SendMessage userMessage = new SendMessage();
            userMessage.setChatId(text);
            if (isAdmin==1) {
                sendMessage.setText("Yangi Admin qo'shildi");
                userMessage.setText("Siz " + user.getFirstName() + " tomonidan admin qilib saylandingiz");
                userMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.MY_BOT.sendMsg(userMessage);
            }else if (isAdmin==2) {
                sendMessage.setText("Adminlik huquqi olib tashlandi");
                userMessage.setText("Sizning  adminlik huquqingiz  " + user.getFirstName() + " tomonidan  olib tashlandi ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
                userMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.MY_BOT.sendMsg(userMessage);
            }
            else {
                sendMessage.setText("""
                         Xatolik yuz berdi
                         Xatolik sabab bo'lishiga olib keluvchi sabablar
                         1.Kiritilgan chatId xato
                         2.Bunday chatId egasi botdan ro'yxatdan o'tmagan
                         """);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            adminStatusMap.put(chatId, AdminStatus.NOTHING);
        } else if (text.equalsIgnoreCase(KeyboardButtonConstants.SEND_AD_TO_ALL_USERS)) {
            adminStatusMap.put(chatId, AdminStatus.AD_SEND_1);

            sendMessage.setText("Reklamani yuboring");
            sendMessage.setChatId(chatId);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        } else if (adminStatusMap.get(chatId).equals(AdminStatus.BLOCKING_P1)) {
            Users checkIsBlocked = object.getUserByChatId(text);
            if (checkIsBlocked.getId() == 0) {
                sendMessage.setText("Bunday User Mavjud emas");
//                status = AdminStatus.NOTHING;
                adminStatusMap.put(chatId, AdminStatus.NOTHING);
            } else if (checkIsBlocked.isBlocked()) {
                sendMessage.setText("Iltimos Blockdan chiqarish sababini kiriting.");
//                status = AdminStatus.BLOCKING_P2_UNBLOCK;
                adminStatusMap.put(chatId, AdminStatus.BLOCKING_P2_UNBLOCK);

                blockingChatId = text;
            } else {
                sendMessage.setText("Iltimos Blocklanish sababini kiriting.");
//                status = AdminStatus.BLOCKING_P2_BLOCK;
                adminStatusMap.put(chatId, AdminStatus.BLOCKING_P2_BLOCK);

                blockingChatId = text;
            }
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (
                adminStatusMap.get(chatId).equals(AdminStatus.BLOCKING_P2_BLOCK) ||
                        adminStatusMap.get(chatId).equals(AdminStatus.BLOCKING_P2_UNBLOCK)
//                status.equals(AdminStatus.BLOCKING_P2_BLOCK)||
//                        status.equals(AdminStatus.BLOCKING_P2_UNBLOCK)
        ) {
            Users checker = object.getUserByChatId(blockingChatId);
            if (checker.getId() == 0) {
                sendMessage.setText("Bunday user mavjud emas");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                object.blockUser(blockingChatId);
                if (adminStatusMap.get(chatId).equals(AdminStatus.BLOCKING_P2_BLOCK)) {
//                if (status.equals(AdminStatus.BLOCKING_P2_BLOCK)) {
                    sendMessage.setText(blockingChatId + " ushbu chatIdli user Blocklandi");
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    sendMessage.setChatId(blockingChatId);
                    sendMessage.setText("Sizning akauntingiz blocklandi" +
                            "\nBlocklanish sababi: " + text);
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());

                } else if (adminStatusMap.get(chatId).equals(AdminStatus.BLOCKING_P2_UNBLOCK)) {
//                } else if (status.equals(AdminStatus.BLOCKING_P2_UNBLOCK)) {
                    sendMessage.setText(blockingChatId + " ushbu chatIdli userdan Block olindi");
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    sendMessage.setChatId(blockingChatId);
                    sendMessage.setText("Sizning akauntingiz blockdan chiqarildi" +
                            "\nBlockdan chiqarish sababi: " + text);
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                }

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
//            status = AdminStatus.NOTHING;
            adminStatusMap.put(chatId, AdminStatus.NOTHING);
        } else {
            sendMessage.setText("Noto'g'ri narsa kiritildi qayta urinib kuring \uD83D\uDD02");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        }
    }

    public static void handleCallback(User user, Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        Integer messageId = message.getMessageId();
        SendMessage sendMessage = new SendMessage();
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        if (data.startsWith(InlineButtonConstants.REPLY_CALL_BACK)) {
            sendMessage.setChatId(chatId);
            String customerChatId = data.split("/")[1];
            System.out.println(customerChatId);
            ComponentContainer.adminAnswerMap.put(chatId, new MessageData(message, customerChatId, messageId));

            sendMessage.setText("Javobingizni kiriting: ");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (data.startsWith(InlineButtonConstants.CONFIRM_AD_CALL_BACK)) {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            int adsId = Integer.parseInt(data.split("/")[1]);
            String adsUserChatId = data.split("/")[2];
            String adsTitle = data.split("/")[3];
            if (object.acceptAd(adsId)) {
                sendMessage.setChatId(adsUserChatId);
                sendMessage.setText("Sizning: " + adsTitle + "  ID-li e'loningiz" + "\n"
                        + "Tasdiqlandi ✅");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                sendMessage.setChatId(chatId);
                sendMessage.setText("E'lonlar taxtasi yangilandi \n E'lon muvaffaqiyatli saqlandi ☑️");
                DbFunctionsImpl.getAdsById(adsId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }else {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Bunday e'lon mavjud emas qayta urinib kuring \uD83D\uDD04");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        } else if (data.startsWith(InlineButtonConstants.REJECT_AD_CALL_BACK)) {
//            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            int adsId = Integer.parseInt(data.split("/")[1]);
            String adsUserChatId = data.split("/")[2];
            String adsTitle = data.split("/")[3];
            object.rejectAd(adsId);
            String str = "Sizning: " + adsTitle + "  nomli e'loningiz" + "\n";
            sendMessage.setChatId(adsUserChatId);

            sendMessage.setText(str + "Rad etildi✅");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

            sendMessage.setChatId(chatId);
            sendMessage.setText("Operatsiya muvaffaqiyatli yakunladi");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        }
    }

}

