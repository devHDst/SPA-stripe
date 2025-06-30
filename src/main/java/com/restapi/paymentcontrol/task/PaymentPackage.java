package com.restapi.paymentcontrol.task;

import lombok.Data;

@Data
public class PaymentPackage {
    private String waitCode;
    private String userId;
    private int servicePrice;
}
