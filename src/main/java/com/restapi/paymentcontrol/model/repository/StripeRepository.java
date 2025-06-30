package com.restapi.paymentcontrol.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restapi.paymentcontrol.model.entity.StripeInfo;

public interface StripeRepository extends JpaRepository<StripeInfo, String>{
    
    @Query(value = "SELECT * FROM stripe_info WHERE wait_code = :waitCode limit 1",nativeQuery = true)
    StripeInfo getStripeInfo(@Param("waitCode") String waitCode);

    @Query(value = "SELECT * FROM stripe_info WHERE stripe_code = :stripeCode limit 1",nativeQuery = true)
    StripeInfo checkStripe(@Param("waitCode") String stirpeCode);
}
