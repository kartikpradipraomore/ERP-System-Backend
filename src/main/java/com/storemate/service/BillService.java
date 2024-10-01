package com.storemate.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BillService {

    ResponseEntity<String> generateBill(Map<String, String> requestMap);

}
