package com.food.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class ItemBasic{
        private String itemId;
        private String itemName;
        private  int quantity;
        private double price;
    }