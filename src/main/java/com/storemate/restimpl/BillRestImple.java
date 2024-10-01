package com.storemate.restimpl;

import com.storemate.constants.StoreMateConstants;
import com.storemate.rest.BillRest;
import com.storemate.service.BillService;
import com.storemate.utils.StoreMateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class BillRestImple implements BillRest {

    @Autowired
    private BillService billService;

    @Override
    public ResponseEntity<String> generateBill(Map<String, String> requestMap) {
        try{
            return billService.generateBill(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}