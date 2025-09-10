package service;

import com.food.client.BankingClient;
import com.food.dto.*;
import com.food.model.Item;
import com.food.model.User;
import com.food.repository.CartRepository;
import com.food.repository.ItemRepository;
import com.food.repository.OrderRepository;
import com.food.repository.UserRepository;
import com.food.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BankingClient bankingClient;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    OrderService orderService;

    @Test
    public void testAddMenuItems(){
        TimePeriod morning = new TimePeriod(LocalTime.of(7, 0), LocalTime.of(10, 0));
        TimePeriod evening = new TimePeriod(LocalTime.of(18, 0), LocalTime.of(21, 0));

        MenuItem menuItem = new MenuItem("Pizza", 250, 10, List.of(morning, evening));
        List<MenuItem> menuItems = List.of(menuItem);
        User user = new User(String.valueOf(2), "Albert", "user2@example.com", "password123",
                "ADMIN");
        //when(userRepository.findById(String.valueOf(2)).get()).thenReturn(user);
        when(userRepository.findById(String.valueOf(2))).thenReturn(java.util.Optional.of(user));
        orderService.addMenuItems(menuItems, String.valueOf(2));
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    public void testAddItemsToMyCart(){
        ItemBasic item1 = new ItemBasic("1", "Pizza", 2, 250);
        ItemBasic item2 = new ItemBasic("2", "Burger", 1, 150);
        MoveToCart moveToCart = new MoveToCart("user123", List.of(item1, item2));
        orderService.addItemsToMyCart(moveToCart);
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void testPlaceOrderAndPurchase(){
        ItemBasic item1 = new ItemBasic("1", "Pizza", 2, 250);
        ItemBasic item2 = new ItemBasic("2", "Burger", 1, 150);

        MoveToCart moveToCart = new MoveToCart("user123", List.of(item1, item2));
        PlaceOrderAndPay orderAndPay = new PlaceOrderAndPay(123456789L, moveToCart);
        orderService.placeOrderAndPurchase(orderAndPay);
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    public void testViewMenuItems(){
        TimePeriod morning = new TimePeriod(LocalTime.of(8, 0), LocalTime.of(10, 0));
        Item item1 = new Item("Pizza", 250, 2, List.of(morning), true, true);
        Item item2 = new Item("Burger", 150, 1, List.of(morning), false, true);
        Item item3 = new Item("Pasta", 200, 1, List.of(morning), true, true);

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2, item3));
        List<Itemz> result = orderService.viewMenuItems();

        //assertEquals(0, result.size());
    }
    @Test
    public void testUpdateAdminDisplay(){
        ItemDisplay display1 = new ItemDisplay("item1", true);
        ItemDisplay display2 = new ItemDisplay("item2", false);
        List<ItemDisplay> itemDisplays = List.of(display1, display2);
        User user = new User(String.valueOf(2), "Albert", "user2@example.com", "password123",
                "ADMIN");
        when(userRepository.findById(String.valueOf(2))).thenReturn(java.util.Optional.of(user));
        orderService.updateAdminDisplay(itemDisplays, String.valueOf(2));
        verify(kafkaTemplate, times(1)).send(eq("item-display-topic"), any());
    }

    @Test
    public void testUpdateItemDisplayInDb(){
        ItemDisplay display1 = new ItemDisplay("item1", true);
        ItemDisplay display2 = new ItemDisplay("item2", false);
        List<ItemDisplay> itemDisplays = List.of(display1, display2);
        orderService.updateItemDisplayInDb(itemDisplays);
    }

}
