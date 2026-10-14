package com.scda.kata_checkout.domain.service

import com.scda.kata_checkout.domain.Cart
import com.scda.kata_checkout.domain.PricingRule
import org.springframework.stereotype.Service

@Service
class CheckoutService {
    fun checkout(pricingRule: List<PricingRule>, cart: Cart): Int {
        return 0
    }
}