package com.company.util;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardButtonUtil {



    public static ReplyKeyboard getAdminMenu() {
        List<KeyboardRow> rowList = getRowList(
                getRow(
                        getButton(KeyboardButtonConstants.USERS_LIST)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.BLOCK_USER)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.WORK_WITH_ADS),
                        getButton(KeyboardButtonConstants.CONFIRM_ADD_FROM_USER)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.SET_ADMIN),
                        getButton(KeyboardButtonConstants.SEND_AD_TO_ALL_USERS)
                ));

        return getMarkup(rowList);
    }
    public static ReplyKeyboard BlockedUserMenu() {
        List<KeyboardRow> rowList = getRowList(
                getRow(
                        getButton(KeyboardButtonConstants.CONTACT_WITH_ADMIN)
                )
        );

        return getMarkup(rowList);

    }
    public static ReplyKeyboard getUserMenu() {
        List<KeyboardRow> rowList = getRowList(
                getRow(
                        getButton(KeyboardButtonConstants.MY_ADS),
                        getButton(KeyboardButtonConstants.SEND_AD_TO_ADMIN)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.SEARCH),
                        getButton(KeyboardButtonConstants.CONTACT_WITH_ADMIN)
                )
                );

        return getMarkup(rowList);
    }


    private static KeyboardButton getButton(String demo) {

        return new KeyboardButton(demo);
    }

    private static KeyboardRow getRow(KeyboardButton... buttons) {
        return new KeyboardRow(List.of(buttons));
    }

    private static List<KeyboardRow> getRowList(KeyboardRow... rows) {
        return List.of(rows);
    }

    private static ReplyKeyboardMarkup getMarkup(List<KeyboardRow> rowList) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }

    public static ReplyKeyboard getContactMenu() {
        KeyboardButton button = getButton("Raqamingizni jo'nating.📲");
        button.setRequestContact(true);

        return getMarkup(getRowList(getRow(button)));
    }

    public static ReplyKeyboard getSearchMenu() {

        List<KeyboardRow> rowList = getRowList(
                getRow(
                        getButton(KeyboardButtonConstants.S_BY_ADDRES),
                        getButton(KeyboardButtonConstants.S_BY_COUNT_HOME)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.S_BY_PRICE),
                        getButton(KeyboardButtonConstants.S_BY_HOME_DATE)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.BACK_MENU)
                )
        );

        return getMarkup(rowList);
    }
}
