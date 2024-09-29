package com.storemate.POJO;

import jakarta.persistence.*;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.*;
import java.io.*;

@NamedQuery(name = "User.findByEmailId" , query = "select u from User u where u.email=:email")

@NamedQuery(name = "User.getAllUsers", query = "select new com.storemate.wrapper.UserWrapper(u.id,u.name,u.contactNumber,u.email,u.status) from User u where u.role='user'")

@NamedQuery(name = "User.getAllAdmins", query = "select u.email from User u where u.role='admin'")

@NamedQuery(name = "User.updateStatus", query = "update User u set u.status=:status where u.id=:id")
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
