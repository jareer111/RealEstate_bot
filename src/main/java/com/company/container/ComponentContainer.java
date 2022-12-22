package com.company.container;


import com.company.Bot.RealEstateBot;
import com.company.entity.MessageData;
import com.company.enums.AdminStatus;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

public class ComponentContainer {
    public static RealEstateBot MY_BOT = null;

    public static String BOT_USERNAME = "";
    public static String BOT_USERNAME2 = "";
    public static String BOT_TOKEN = "";
    public static String BOT_TOKEN2 = "";
    public static final String USER_DB = "";
    public static final String USER_DB2 = "";
    public static final String PASSWORD_DB = "";
    public static final String PASSWORD_DB2 = "";
    public static final String URL_DB = "";
    public static final String URL_DB2 = "";
    public static final String DATABASE = "";
    public static final String HOST = "";
    public static final String BASE_FOLDER = "";
    public static final String CHANEL_ID = "";
    public static Map<String, AdminStatus> adminStatusMap = new HashMap<>();
    public static final Map<Integer, String> BASE_MATERIAL_HOME = new HashMap<>();
    public static final Map<Integer, String> BASE_SALE_TYPE = new HashMap<>();
    public static final Map<Integer, String> BASE_PRICE_NAME = new HashMap<>();
    public static final String BASE_QUERY = """
            select ads.photo_path, ht.name, ads.updated_at as "updated time",
                   ads.phone_number, st.name as "sale name", d.name as "district name",reg.name, par.room_count as "room count", par.area, par.floor,par.max_floor,m.name, ads.price "price",
                   cur.name "currency type",ads.info "description", ads.id, u.chat_id, ads.title from ads join users u
                 on ads.user_id = u.id join sale_type st on ads.sale_type_id = st.id join district d on ads.district_id = d.id join region reg on reg.id = d.region_id
                 join parametr par on  ads.parameter_id = par.id join currency cur on ads.price_type_id = cur.id join home_type ht on
                    ads.home_type_id=ht.id join ads_status stat on ads.status_id = stat.id join material m on par.material_id= m.id where 
            """;

    {
        adminStatusMap.put("1223201050", AdminStatus.NOTHING);
    }

    public static List<String>adminList=new LinkedList<>();

    public static Map<String, Boolean> customerMap = new HashMap<>();
    public static Map<String, MessageData> adminAnswerMap = new HashMap<>();


}
