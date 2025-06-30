package com.restapi.paymentcontrol.Controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.paymentcontrol.Payload.StripePayload;
import com.restapi.paymentcontrol.Payload.StripePrePayload;
import com.restapi.paymentcontrol.model.entity.StripeInfo;
import com.restapi.paymentcontrol.model.repository.StripeRepository;
import com.restapi.paymentcontrol.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/Stripe")
public class StripeInfoController {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final String SUCCESS_MSG = "予約確認いたしました。キャンセルの場合はメールを確認ください";
    private static final String INTENT_NOT_FOUND_MSG = "決済情報の取得に失敗しました";
    private static final String CUSTOMER_NOT_FOUND_MSG = "顧客情報の取得に失敗しました";


    @Autowired
    StripeRepository stripeRepository;
    @Autowired
    PaymentService paymentService;


    @PostMapping("/preregister")
    public Map<String, String> postMethodName(@RequestBody StripePrePayload prePayload) throws StripeException {
        
        // Stripeへの仮処理
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount((long) prePayload.getAmount())
            .setCurrency(prePayload.getCurrency())
            .build();
        PaymentIntent intent = PaymentIntent.create(params);
        log.info("PaymentIntent Created");
        Map<String, String> response = new HashMap<>();
        response.put("intentId", intent.getId());
        response.put("clientSecret", intent.getClientSecret());

        return response;
    }
    

    @PostMapping("/register")
    public Map<String, String> registerStripeInfo(@RequestBody StripePayload payload) throws Exception{

        // インテントの引き出し
        System.out.println("インテンド"+payload.getIntentId());
        PaymentIntent intent = PaymentIntent.retrieve(payload.getIntentId());

        // レスポンス雛形
        Map<String, String> response = new HashMap<>();

        if(intent == null) 
        {
            log.info("no PaymentIntent");
            response.put("message",StripeInfoController.INTENT_NOT_FOUND_MSG);
            return response;
        }
        // 顧客情報の引き出し
        Customer customer = paymentService.createCustomer(payload.getUserId());
        if(customer == null) {
            log.info("failed Creation of Customer");
            response.put("message", StripeInfoController.CUSTOMER_NOT_FOUND_MSG);
            return response;
        } 
        // インテントの更新を実施
        PaymentIntentUpdateParams params = PaymentIntentUpdateParams.builder()
        .setCustomer(customer.getId())
        .setPaymentMethod(payload.getPaymentMethod())
        .setReceiptEmail(payload.getUserId())
        .build();
        log.info("updateIntentParams");
        intent.update(params);
        try {
            // 予約決済情報を保存
            StripeInfo registInfo = new StripeInfo();
            registInfo.setUserId(payload.getUserId());
            registInfo.setWaitCode(payload.getWaitCode());
            registInfo.setStripeCusCode(customer.getId());
            registInfo.setIntentCode(payload.getIntentId());
            registInfo.setPaymentCode(payload.getPaymentMethod());
            registInfo.setExpDate(payload.getExpDateTime());
            PaymentMethod result = paymentService.attachCus2PaymentMethod(registInfo, customer);
            // if(result.getId().equals(null)){
            // 判定うまくいかないのでStripeからのオブジェクトの仕様を確認
            //     return "顧客情報の紐付けに失敗しました";
            // }
            stripeRepository.save(registInfo);
            log.info("Stripe Info Reserved:" + result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.put("message", "予約情報の登録に失敗しました");
            return response;
        }
        response.put("message", StripeInfoController.SUCCESS_MSG);
        return response;
    }
    
}
