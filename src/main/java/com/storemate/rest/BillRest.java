package com.storemate.rest;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/bill")
public interface BillRest {

    @PostMapping(path = "/generateBill")
    ResponseEntity<String> generateBill(@RequestBody Map<String, String> requestMap);

}
