package com.company.entity;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Parameter {
    private int id;
    private int roomCount;
    private int area;
    private BigDecimal livingArea;
    private int floor;
    private Integer maxFloor;
    private int materialId;
    private int statusId;
}
