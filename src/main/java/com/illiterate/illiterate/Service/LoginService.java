package com.illiterate.illiterate.Service;

import com.illiterate.illiterate.Entity.User;
import com.illiterate.illiterate.Repository.UserRepository;

public class LoginService {
    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String Login(String userid, String userpw){
        User user = new User();
        String id = user.getUserid();
        String password = user.getPassword();

        if(id.matches(userid) && password.matches(userpw)){
            return "ok";
        }else{
            return "no";
        }
    }
}
