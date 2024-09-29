package com.storemate.POJO;

import jakarta.persistence.*;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.*;
import java.io.*;

@NamedQuery(name = "User.findByEmailId" , query = "select u from User u where u.email=:email")
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String contactNumber;
    private String email;
    private String password;
    private String status;
    private String role;


}
