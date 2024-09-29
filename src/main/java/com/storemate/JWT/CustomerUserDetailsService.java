package com.storemate.JWT;

import com.storemate.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

   @Autowired
   UserDao userDao;

   private com.storemate.POJO.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userDetail = userDao.findByEmailId(username);
        if(!Objects.isNull(userDetail)) {
            return  new User(userDetail.getEmail(),userDetail.getPassword(),new ArrayList<>());
        }else{
            throw new UsernameNotFoundException("User not found");
        }
    }

    public com.storemate.POJO.User getUserDetails(){
        return userDetail;
    }
}
