package com.restapi.paymentcontrol.model.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "StripeInfo")
@Entity
public class StripeInfo {
    
    @Id
    @Column(name = "userId",nullable = false,unique = true)
    private String userId;

    @Column(name = "waitCode",nullable = false , unique = true)
    private String waitCode;

    @Column(name = "stripeCusCode", unique = true)
    private String stripeCusCode;

    @Column(name = "intentCode", unique = true)
    private String intentCode;

    @Column(name = "paymentCode", unique = true)
    private String paymentCode;

    @Column(name = "expDate",nullable = false)
    private Date expDate;

    public StripeInfo(){
    }

    public StripeInfo(String userId,String waitCode, String stripeCusCode, String intentCode, String paymentCode, Date expDate ){
        this.userId = userId;
        this.waitCode = waitCode;
        this.stripeCusCode = stripeCusCode;
        this.intentCode = intentCode;
        this.paymentCode = paymentCode;
        this.expDate = expDate;
    }
}
