package com.food.model;

import com.food.dto.Itemz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "orders")
public class Order {
    public Order(String userId,Double overallPrice, List<Itemz> itemzs) {
        this.userId = userId;
        this.itemzs = itemzs;
        this.overallPrice = overallPrice;
    }

    @Id
    private String orderId;
    private String userId;
    private Double overallPrice;
    private List<Itemz> itemzs;

}

