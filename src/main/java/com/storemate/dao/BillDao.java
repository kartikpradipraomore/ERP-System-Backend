package com.storemate.dao;

import com.storemate.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDao extends JpaRepository<Bill, Integer> {

}
