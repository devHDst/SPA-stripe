package com.restapi.paymentcontrol.service;

import com.restapi.paymentcontrol.model.entity.StripeInfo;
import com.restapi.paymentcontrol.model.repository.StripeRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    StripeRepository stripeRepository;

    public PaymentService(@Value("${stripe.key.secret}") String secretKey){
        Stripe.apiKey = secretKey;
    }

    public Customer createCustomer(String userId) throws StripeException{
        try {
            // 顧客情報を作成
            CustomerCreateParams customerBase = CustomerCreateParams.builder()
                .setName(userId)
                .build();
            return Customer.create(customerBase);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public PaymentMethod attachCus2PaymentMethod(StripeInfo stripeInfo, Customer stripeCustomer)throws Exception{   
        //顧客情報を紐付け
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(stripeInfo.getPaymentCode());
            PaymentMethodAttachParams attachParams = PaymentMethodAttachParams
            .builder()
            .setCustomer(stripeCustomer.getId())
            .build();
            return paymentMethod.attach(attachParams);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public PaymentIntent excutePayment(StripeInfo stripeInfo, String customerId,int price) throws Exception{
        // 決済処理を実行
        if(stripeInfo.getExpDate().after(new Date())){
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount((long) price)
            .setCurrency("jpy")
            .setCustomer(customerId)
            .setPaymentMethod(stripeInfo.getPaymentCode())
            .setConfirm(true)
            .build();
            return PaymentIntent.create(params);
        }
        return null;
    }

    public PaymentIntent createPaymentIntent(String waitCode, int price) throws Exception{

        StripeInfo stripeInfo = stripeRepository.getStripeInfo(waitCode);
        Customer.retrieve(stripeInfo.getStripeCusCode());
        
        if(!stripeInfo.getStripeCusCode().isEmpty()){
            log.info(":顧客データ確認");
            try { 
                // 予約した顧客情報を引き出し
                PaymentIntent executeIntent = PaymentIntent.retrieve(stripeInfo.getIntentCode());
                if(!executeIntent.getId().isEmpty()){
                    log.info(":登録済みインテント確認");
                    // 決済処理を実行
                    return executeIntent.confirm();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
