package com.localmart.retailer;

import lombok.Data;

@Data
public class RetailerRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
}
