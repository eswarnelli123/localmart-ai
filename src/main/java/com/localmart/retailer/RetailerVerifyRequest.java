package com.localmart.retailer;

import lombok.Data;

@Data
public class RetailerVerifyRequest {
    private String email;
    private String otp;
}
