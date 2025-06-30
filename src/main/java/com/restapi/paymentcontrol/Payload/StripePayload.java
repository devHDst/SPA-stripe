package com.restapi.paymentcontrol.Payload;

import java.sql.Date;

import lombok.Data;

@Data
public class StripePayload {
    private String userId;
    private String waitCode;
    private String intentId;
    private String paymentMethod;
    private Date expDateTime;
}
