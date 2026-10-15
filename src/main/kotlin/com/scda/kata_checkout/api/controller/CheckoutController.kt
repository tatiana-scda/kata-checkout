package com.scda.kata_checkout.api.controller

import com.scda.kata_checkout.api.model.CheckoutRequest
import com.scda.kata_checkout.api.model.CheckoutResponse
import com.scda.kata_checkout.domain.Cart
import com.scda.kata_checkout.domain.PricingRule
import com.scda.kata_checkout.service.CheckoutService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/v1/checkout")
class CheckoutController(private val checkoutService: CheckoutService) {

    @PostMapping
    fun checkout(@RequestBody request: CheckoutRequest): CheckoutResponse {
        val itemById = request.itemById()
        val pricingRules: List<PricingRule>
        val cart: Cart
        try {
            pricingRules = request.pricingRules.map { it.toDomain(itemById) }
            cart = request.cart.toDomain(itemById)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        }

        return CheckoutResponse(checkoutService.checkout(pricingRules, cart))
    }
}
