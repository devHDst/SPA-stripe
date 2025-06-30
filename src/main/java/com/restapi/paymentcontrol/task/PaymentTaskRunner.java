package com.restapi.paymentcontrol.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.restapi.paymentcontrol.service.PaymentService;
import com.stripe.model.PaymentIntent;

@Component
public class PaymentTaskRunner implements CommandLineRunner{
    
    private final static Logger log = LoggerFactory.getLogger(CommandLineRunner.class);

    @Autowired
    private PaymentService paymentService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receivePaymentQueue(PaymentPackage queuePackage) throws Exception{
        try {
            log.info(":get queue info");
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(queuePackage.getWaitCode(),queuePackage.getServicePrice());
            String paymentStr = paymentIntent.toJson();
            log.info("Complete Stripe Info:" + paymentStr);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
    }

    @Override
    public void run(String... args) throws Exception {
        // PaymentIntent paymentIntent = paymentService.createPaymentIntent();
        // PaymentIntent.retrieve(paymentIntent.getId());
        System.out.println("アプリケーションが起動しました");
    }
}
