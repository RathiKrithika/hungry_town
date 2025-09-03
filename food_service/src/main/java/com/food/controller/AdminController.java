package com.food.controller;

import com.food.dto.ItemDisplay;
import com.food.dto.Login;
import com.food.dto.MenuItem;
import com.food.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {
   @Autowired
    OrderService orderService;
    @PostMapping("/addMenuItems/{userId}")
    public void login(@RequestBody List<MenuItem> menuItems,
                      @PathVariable("userId") String userId){
        orderService.addMenuItems(menuItems, userId);
    }

    @PostMapping("/admin/update/itemDisplay/{userId}")
    public void itemDisplayControl(@RequestBody List<ItemDisplay> itemDisplays,
                                   @PathVariable("userId") String userId){
        orderService.updateAdminDisplay(itemDisplays, userId);

    }
}
