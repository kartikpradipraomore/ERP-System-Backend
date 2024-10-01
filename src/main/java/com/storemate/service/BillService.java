package com.storemate.service;

import com.storemate.POJO.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BillService {

    ResponseEntity<String> generateBill(Map<String, String> requestMap);

    ResponseEntity<List<Bill>> getBills();

    ResponseEntity<byte[]> getPdf(Map<String, String> requestMap);

    ResponseEntity<String> deleteBill(Integer id);


}
