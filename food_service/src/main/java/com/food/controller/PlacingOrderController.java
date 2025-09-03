package com.food.controller;

import com.food.dto.Itemz;
import com.food.dto.MoveToCart;
import com.food.dto.PlaceOrderAndPay;
import com.food.model.Order;
import com.food.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlacingOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/menu")
    public List<Itemz> viewMenu(){
       return orderService.viewMenuItems();
    }
    @PostMapping("/addToCart")
    public void addItemToMyCart(@RequestBody MoveToCart moveToCart){
        orderService.addItemsToMyCart(moveToCart);
    }
    @PostMapping("/placeMyOrder")
    public void placeMyOrder(@RequestBody PlaceOrderAndPay placeAndPay){
        orderService.placeOrderAndPurchase(placeAndPay);
    }

}
