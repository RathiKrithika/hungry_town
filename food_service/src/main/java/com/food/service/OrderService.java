package com.food.service;

import com.food.client.BankingClient;
import com.food.client.PaymentRequest;
import com.food.dto.*;
import com.food.exception.UserNotFoundException;
import com.food.model.Cart;
import com.food.model.Item;
import com.food.model.Order;
import com.food.model.User;
import com.food.repository.CartRepository;
import com.food.repository.ItemRepository;
import com.food.repository.OrderRepository;
import com.food.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private BankingClient bankingClient;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    @Transactional
    public void addMenuItems(List<MenuItem> menuItems, String userId) {
        User user = userRepository.findById(userId).get();
        if(user.getRole() == "ADMIN"){
            menuItems.stream().forEach(e -> {
                itemRepository.save(new Item(
                        e.getName(),
                        e.getPrice(),
                        e.getQuantity(),
                        e.getTimeLimits(),
                        true,
                        true
                ));
            });
        }
        else throw new UserNotFoundException();

    }

    @Transactional
    public void addItemsToMyCart(MoveToCart moveToCart){
        cartRepository.save(new Cart(moveToCart.getUserId(), formItemz(moveToCart)));
    }
public List<Itemz> formItemz(MoveToCart moveToCart){
    return moveToCart.getItemBasics().stream().
            map(e -> new Itemz(e.getItemName(), e.getQuantity(),
                    e.getPrice() * e.getQuantity())).collect(Collectors.toList());
}
    @Transactional
    public void placeOrderAndPurchase(PlaceOrderAndPay placeOrderAndPay){
        MoveToCart moveToCart = placeOrderAndPay.getMoveToCart();
        List<Itemz> itemzs = formItemz(moveToCart);
        List<ItemBasic> itemBasics = moveToCart.getItemBasics();

        //calculate total amount to reduce from bank account
        double grandTotal = itemzs.stream()
                .mapToDouble(Itemz::getTotalPrice)
                .sum();

        orderRepository.save(new Order(moveToCart.getUserId(),grandTotal,itemzs));
        PaymentRequest paymentRequest = new PaymentRequest(placeOrderAndPay.getFromAccountNo(), grandTotal, "Food");
        bankingClient.transfer(paymentRequest);
    }

    @Transactional
    public void  updateMenuDisplay(List<Item> items) {
        LocalTime now = LocalTime.now();
        for (Item item : items) {
            item.updateDisplay(now);
        }
        itemRepository.saveAll(items);
    }

    @Transactional
    public List<Itemz> viewMenuItems(){
        List<Item> overallItems = itemRepository.findAll();
        updateMenuDisplay(overallItems);
        return overallItems.stream()
                .filter(item -> item.isDisplay() && item.isAdminDisplay())
                .map(item -> {
                    Itemz itemz = new Itemz();
                    itemz.setItemName(item.getName());
                    itemz.setQuantity(item.getQuantity());
                    itemz.setTotalPrice(item.getPrice());
                    return itemz;
                })
                .collect(Collectors.toList());

    }

    @Transactional
    public void updateAdminDisplay(List<ItemDisplay> itemDisplays, String userId){
        User user = userRepository.findById(userId).get();
        if(user.getRole().equals("ADMIN")){
            kafkaTemplate.send("item-display-topic", itemDisplays);
        }
        else throw new UserNotFoundException();
    }

    @KafkaListener(topics = "item-display-topic", groupId = "shopping-group", containerFactory = "kafkaListenerContainerFactory")
    public void updateItemDisplayInDb(List<ItemDisplay> itemDisplays){
        for (ItemDisplay itemDisplay : itemDisplays) {
            itemRepository.findById(itemDisplay.getItemId()).ifPresent(item -> {
                item.setAdminDisplay(itemDisplay.isDisplay());
                itemRepository.save(item);
            });
        }
    }
}

