package com.storemate.serviceimpl;

import com.storemate.JWT.CustomerUserDetailsService;
import com.storemate.JWT.JwtFilter;
import com.storemate.JWT.JwtUtil;
import com.storemate.POJO.User;
import com.storemate.constants.StoreMateConstants;
import com.storemate.dao.UserDao;
import com.storemate.service.UserService;
import com.storemate.utils.StoreMateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {

        log.info("inside signup{}", requestMap);
        try {
            if (validateSignupMap(requestMap)) {

                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return StoreMateUtils.getResponseEntity("Registeration Successful", HttpStatus.OK);
                } else {
                    return StoreMateUtils.getResponseEntity("Email Already Exits.", HttpStatus.BAD_REQUEST);
                }

            } else {
                return StoreMateUtils.getResponseEntity(StoreMateConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }



    private boolean validateSignupMap(Map<String, String> requestMap) {

        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String,String> requestMap){
    User user = new User();
    user.setName(requestMap.get("name"));
    user.setContactNumber(requestMap.get("contactNumber"));
    user.setEmail(requestMap.get("email"));
    user.setPassword(requestMap.get("password"));
    user.setStatus("false");
    user.setRole("user");
    return user;
    }

    //=====================================================================================================================================

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
       try{

           Authentication auth = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
           );

                   if(auth.isAuthenticated()){
                        if(customerUserDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")){
                            return new ResponseEntity<String>("{\"token\":\""+
                                    jwtUtil.genrateToken(customerUserDetailsService.getUserDetails().getEmail(),customerUserDetailsService.getUserDetails().getRole())+"\"}", HttpStatus.OK);
                        }else{
                            return StoreMateUtils.getResponseEntity("Wait For Admin Approval", HttpStatus.BAD_REQUEST);
                        }
                   }


       } catch (Exception e) {
           e.printStackTrace();
       }
        return StoreMateUtils.getResponseEntity("Bad Credential", HttpStatus.BAD_REQUEST);
    }


}
