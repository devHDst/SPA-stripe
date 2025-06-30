package com.restapi.paymentcontrol.Payload;

import lombok.Data;

@Data
public class StripePrePayload {
    private int amount;
    private String currency;
}
