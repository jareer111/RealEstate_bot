package com.company.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Photo1 {
    private int id;
    private String url;
    private long adsId;
    private boolean isMain;
}
