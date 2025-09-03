package com.food.service;

import com.food.dto.CreateUser;
import com.food.dto.Login;
import com.food.exception.PasswordMismatchException;
import com.food.exception.UserNotFoundException;
import com.food.model.User;
import com.food.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String register(CreateUser u){
        String encodedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
        User user = new User(u.getContact(),u.getEmail(),encodedPassword,u.getRole());
        User save = userRepository.save(user);
        return save.getId();
    }

    @Transactional
    public String login(Login login){
        User user = Optional.ofNullable(userRepository.findByEmail(login.getEmail()))
                .orElseThrow(() -> new UserNotFoundException(login.getEmail()));

       boolean matches = validatePassword(user.getPassword(), login.getPassword());
        if(!matches) throw new PasswordMismatchException(user.getEmail());

        return "Login successful";
    }

    public boolean validatePassword(String dbPassword, String userTypedPswrd) {
        String password = new StringBuffer(dbPassword).replace(0, 3, "$2a").toString();
        boolean matches = BCrypt.checkpw(userTypedPswrd, password);
        return matches;
    }

}
