package com.food.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriod {

    private LocalTime start; // e.g., 07:00
    private LocalTime end;   // e.g., 10:00

    // Check if a given time falls in this period
    public boolean isWithin(LocalTime time) {
        return !time.isBefore(start) && !time.isAfter(end);
    }


}
