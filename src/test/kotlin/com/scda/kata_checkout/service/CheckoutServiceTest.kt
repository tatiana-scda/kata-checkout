package com.scda.kata_checkout.service

import com.scda.kata_checkout.domain.Cart
import com.scda.kata_checkout.domain.Item
import com.scda.kata_checkout.domain.UnitPriceRule
import com.scda.kata_checkout.domain.service.CheckoutService
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckoutServiceTest {

    private val checkoutService = CheckoutService()
    
    private val itemA = Item("A", 50)
    private val itemB = Item("B", 75)
    private val itemC = Item("C", 25)
    private val itemD = Item("D", 150)
    private val itemE = Item("E", 200)

    private val pricingRules = listOf(itemA, itemB, itemC, itemD, itemE)
        .map(::UnitPriceRule)

    @Test
    fun `checkout with an empty cart returns zero`() {
        val cart = Cart(mutableMapOf())

        val total = checkoutService.checkout(pricingRules, cart)

        assertEquals(0, total)
    }

    @Test
    fun `checkout with a single item returns its unit price`() {
        val cart = Cart(mutableMapOf(itemA to 1))

        val total = checkoutService.checkout(pricingRules, cart)

        assertEquals(50, total)
    }

    @Test
    fun `checkout with multiple different items returns the sum of unit prices`() {
        val cart = Cart(
            mutableMapOf(
                itemA to 1,
                itemB to 1,
                itemC to 1,
            )
        )

        val total = checkoutService.checkout(pricingRules, cart)

        assertEquals(50 + 75 + 25, total)
    }

    @Test
    fun `checkout with quantities multiplies each item price`() {
        val cart = Cart(
            mutableMapOf(
                itemD to 2,
                itemE to 3,
            )
        )

        val total = checkoutService.checkout(pricingRules, cart)

        assertEquals(2 * 150 + 3 * 200, total)
    }
}
