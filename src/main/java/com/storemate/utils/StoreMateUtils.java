package com.storemate.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class StoreMateUtils {

    private StoreMateUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus status){
        return new  ResponseEntity<String>("{\"message\":\""+message+"\"}" , status);
    }


}
