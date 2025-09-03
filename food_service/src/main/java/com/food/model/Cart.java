package com.food.model;

import com.food.dto.Itemz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cart")
public class Cart {

    @Id
    private String cartId;
    private String userId;
    private List<Itemz> itemzs;

    public Cart(String userId, List<Itemz> itemzs) {
        this.userId = userId;
        this.itemzs = itemzs;
    }
}