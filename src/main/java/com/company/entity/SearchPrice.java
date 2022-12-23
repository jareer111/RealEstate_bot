package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchPrice {
    private double price1;
    private double price2;
    private int saleTypeId;
    private int districtId;
    private int regionId;

}
