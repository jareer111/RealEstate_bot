package com.company.files;

import com.company.entity.Ads;
import com.company.entity.Users;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.List;

public interface WorkWithDbFunctions {

    Users getUserByChatId(String chatId);

    boolean addAdvertisement(Ads ads);

    List<Users> getAllUsers();

    boolean setOrRemoveAdmin(String chatId,boolean status);

    boolean removeAdmin(String chatId);

    Users addUser(String chatId, Contact contact,String userName);

    void getMyAdsHistory(String chatId);

    void getAdsToCheck(String chatId);

    List<String> getAdminsChatIds();

    boolean blockUser(String text);

    int isAdsEmpty(String chatId);

    boolean acceptAd(int adsId);

    boolean rejectAd(int adsId);



}
