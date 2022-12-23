package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Region {
    private int id;
    private String name;

    public Region(int id) {
        this.id = id;
    }
}
