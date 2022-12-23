package com.company.entity;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ads {
    private int id;
    private long userId;
    private String adType;
    private String title;
    private Timestamp updatedAt;
    private String phoneNumber;
    private int saleTypeId;
    private int districtId;
    private int parameterId;
    private BigDecimal price;
    private int priceTypeId;
    private String info;
    private int homeTypeId;
    private int statusId;
    private String photoPath;

    public Ads(long user_id, String adType, String title,
               Timestamp updatedAt, String phoneNumber,
               int saleTypeId, int districtId, int parameterId,
               BigDecimal price, int priceTypeId, String info,
               int homeTypeId, int statusId) {
        this.userId = user_id;
        this.adType = adType;
        this.title = title;
        this.updatedAt = updatedAt;
        this.phoneNumber = phoneNumber;
        this.saleTypeId = saleTypeId;
        this.districtId = districtId;
        this.parameterId = parameterId;
        this.price = price;
        this.priceTypeId = priceTypeId;
        this.info = info;
        this.homeTypeId = homeTypeId;
        this.statusId = statusId;
    }
}
