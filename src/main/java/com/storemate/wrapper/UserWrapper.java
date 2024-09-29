package com.storemate.wrapper;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWrapper {

    private Integer id;

    private String name;

    private String contactNumber;

    private String email;

    private String status;

    public UserWrapper(Integer id, String name, String contactNumber, String email, String status) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.status = status;
    }
}
