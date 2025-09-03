package com.food.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuItem {
    private String name;
    private double price;
    private int quantity;
    private List<TimePeriod> timeLimits;

}
