package com.storemate.POJO;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.io.Serializable;


@NamedQuery(name = "Product.getAllProduct", query = "select new com.storemate.wrapper.ProductWrapper(p.id, p.name, p.description,p.price,p.status,p.category.id, p.category.name) from Product p")

@NamedQuery(name = "Product.updateProductStatus", query = "update Product p set p.status=:status where p.id=:id")

@NamedQuery(name = "Product.getProductByCategory", query = "select new com.storemate.wrapper.ProductWrapper(p.id,p.name) from Product p where p.category.id=:id and  p.status='true' ")

@NamedQuery(name = "Product.getProductById", query = "select new com.storemate.wrapper.ProductWrapper(p.id,p.name,p.description,p.price) from Product p where p.id=:id and p.status='true'")
@Data
@DynamicInsert
@DynamicUpdate
@Entity
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    private String description;

    private Integer price;

    private String status;




}
