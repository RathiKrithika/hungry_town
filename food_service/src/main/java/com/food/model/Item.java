package com.food.model;

import com.food.dto.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "items")
public class Item {
    public Item(String name, double price, int quantity, List<TimePeriod> timeLimits, boolean display, boolean adminDisplay) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.timeLimits = timeLimits;
        this.display = display;
        this.adminDisplay = adminDisplay;
    }



    @Id
    private String id;
    private String name;
    private double price;
    private int quantity;
    // Store time periods when this item is available
    private List<TimePeriod> timeLimits;
    private boolean display;
    private boolean adminDisplay;

    // Helper to check if item should be displayed
    public void updateDisplay(LocalTime currentTime) {
        this.display = timeLimits.stream().anyMatch(tp -> tp.isWithin(currentTime));
    }
}
