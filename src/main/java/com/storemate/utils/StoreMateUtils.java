package com.storemate.utils;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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

    public static JSONArray getJsonArrFromString(String data)throws JSONException {

        JSONArray jsonArr = new JSONArray(data);
        return jsonArr;

    }

    public static Map<String,Object> getMapFromJson(String data){
        if(!Strings.isNullOrEmpty(data)) {
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {}.getType());

        }
        return new HashMap<>();
    }

    public static Boolean isFileExist(String path){
        log.info("Inside isFileExist",path);
        try{
            File file = new File(path);
            return file.exists() ? Boolean.TRUE : Boolean.FALSE;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }


}
