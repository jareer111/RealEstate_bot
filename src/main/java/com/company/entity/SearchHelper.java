package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchHelper {
    private String adParameterId;
    private Integer numberOfRooms;
    private Integer districtId;
    private Integer regionId;

}