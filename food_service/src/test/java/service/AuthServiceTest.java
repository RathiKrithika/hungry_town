package service;


import com.food.dto.CreateUser;
import com.food.dto.Login;
import com.food.exception.UserNotFoundException;
import com.food.model.User;
import com.food.repository.UserRepository;
import com.food.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    AuthService service;

    @Test
    public void testRegister(){
        CreateUser c = new CreateUser("Krithika", "krithika@gmail.com","9898989898","Krithika@123");
       when(userRepository.save(any(User.class))).thenReturn(new User(String.valueOf(2),"Krithika", "krithika@gmail.com","9898989898",""));

        String userId = service.register(c);
        verify(userRepository).save(any(User.class));
        assertEquals(String.valueOf(2), userId);
    }

    @Test
    public void testRegisterWithExistingEmailId(){
        CreateUser c = new CreateUser("Krithika", "krithika@gmail.com","9898989898","Krithika@123");

        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate",
                        new SQLException("violates unique constraint \"unique_email_only\"")));

        assertThrows(DataIntegrityViolationException.class, () -> {
           service.register(c);
        });
        }

    /*@Test
    public void login(){
        Login l = new Login("albert@gmail.com","albert@123");
        User user = new User(String.valueOf(2), "Albert", "user2@example.com", "password123",
                "$2a$10$nc0MIH6VE25o//vHk7.t9OVuUYUTRjRsRpgpjrSbJn4IJdI8tQ0cC");
        when(userRepository.findByEmail(l.getEmail())).thenReturn(user);
        String msg = service.login(l);
        assertEquals(msg, "Login successful");
    }*/

    @Test
    public void falseUserLogin(){
        Login l = new Login("albert@gmail.com","albert@123");
        when(userRepository.findByEmail(l.getEmail())).thenReturn(null);

        assertThrows(UserNotFoundException.class,()->{
            service.login(l);
        });
    }
}
