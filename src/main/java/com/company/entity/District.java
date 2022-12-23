package com.company.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class District {
    private int id;
    private int regionId;
    private String name;
}
