package com.storemate.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class StoreMateUtils {

    private StoreMateUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus status){
        return new  ResponseEntity<String>("{\"message\":\""+message+"\"}" , status);
    }

    public static String getUuid(){
        Date date = new Date();
        long time = date.getTime();
        return "BILL-"+time;
    }


}
