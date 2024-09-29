package com.storemate.service;

import com.storemate.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    public ResponseEntity<String> signUp(Map<String, String> requestMap);

    public ResponseEntity<String> login(Map<String, String> requestMap);

    public ResponseEntity<List<UserWrapper>> getAllUsers();

    public ResponseEntity<String> update(Map<String, String> requestMap);

}


