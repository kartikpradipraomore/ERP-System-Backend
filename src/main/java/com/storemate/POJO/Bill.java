package com.storemate.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Bill.getAllBills",  query = "select b from Bill b order by b.id desc")

@NamedQuery(name = "Bill.getBillByUserName", query = "select b from Bill b where b.createdBy=:username order by b.id desc")

@Data
@DynamicInsert
@DynamicUpdate
@Entity
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uuid;

    private String name;

    private String email;

    private String contactNumber;

    private String paymentMethod;

    private Integer total;

    @Column(name = "productdetails", columnDefinition = "json")
    private String productDetails;

    private String createdBy;

}
