package com.storemate.dao;

import com.storemate.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);

}



