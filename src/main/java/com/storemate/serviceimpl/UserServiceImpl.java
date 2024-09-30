package com.storemate.serviceimpl;

import com.storemate.JWT.CustomerUserDetailsService;
import com.storemate.JWT.JwtFilter;
import com.storemate.JWT.JwtUtil;
import com.storemate.POJO.User;
import com.storemate.constants.StoreMateConstants;
import com.storemate.dao.UserDao;
import com.storemate.service.UserService;
import com.storemate.utils.EmailUtils;
import com.storemate.utils.StoreMateUtils;
import com.storemate.wrapper.UserWrapper;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;


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

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;


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

    //============================================================================================================================

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try {

            if(jwtFilter.isAdmin()){
               return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new  ResponseEntity<List<UserWrapper>>(new ArrayList<UserWrapper>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {

            if(jwtFilter.isAdmin()){
               Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
               if (!optional.isEmpty()){
                        userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                        sendEmailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmins());
                        return StoreMateUtils.getResponseEntity("Status Updated Successful", HttpStatus.OK);
               }else{
                   return StoreMateUtils.getResponseEntity("User Id Doesn't Exists", HttpStatus.OK);
               }
            }else {
                return StoreMateUtils.getResponseEntity(StoreMateConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void sendEmailToAllAdmin(String status, String user, List<String> allAdmins) {

        allAdmins.remove(jwtFilter.getCurrUserName());

        if(status != null && status.equalsIgnoreCase("true")){

            emailUtils.sendSimpleMail(jwtFilter.getCurrUserName(), "Account Approved", "user:-"+ user +" \n Is Approved by \n ADMIN-:"+jwtFilter.getCurrUserName(), allAdmins);

        }else{
            emailUtils.sendSimpleMail(jwtFilter.getCurrUserName(), "Account Disabled", "user:-"+ user +" \n Is  Disabled by \n ADMIN-:"+jwtFilter.getCurrUserName(), allAdmins);

        }

    }

    @Override
    public ResponseEntity<String> checkToken() {
        return StoreMateUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
       try{
            User user = userDao.findByEmail(jwtFilter.getCurrUserName());

            if(!user.equals(null)){
                if(user.getPassword().equals(requestMap.get("oldPassword"))){
                    user.setPassword(requestMap.get("newPassword"));
                    userDao.save(user);
                    return StoreMateUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return StoreMateUtils.getResponseEntity("INCORRECT OLD PASSWORD", HttpStatus.BAD_REQUEST);
            }
            return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);

       } catch (Exception e) {
           e.printStackTrace();
       }

       return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // ==================================================================================================================================================

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            log.info("Fetched user: {}", user);

            if (!Objects.isNull(user)) {
                emailUtils.forgetMail(user.getEmail(), "Forgot Password Information By StoreMate", user.getPassword());

            } else {
                return StoreMateUtils.getResponseEntity("User with the provided email does not exist", HttpStatus.BAD_REQUEST);
            }
            return StoreMateUtils.getResponseEntity("Check Your Email For Credentials", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred in forgetPassword: ", e);  // Better error logging
            return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
