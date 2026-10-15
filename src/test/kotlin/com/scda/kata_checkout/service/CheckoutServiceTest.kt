package com.scda.kata_checkout.service

import com.scda.kata_checkout.domain.BuyBulkRule
import com.scda.kata_checkout.domain.BuyGetFreeRule
import com.scda.kata_checkout.domain.Cart
import com.scda.kata_checkout.domain.Item
import com.scda.kata_checkout.domain.MealDealRule
import com.scda.kata_checkout.domain.UnitPriceRule
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckoutServiceTest {

    private val checkoutService = CheckoutService()
    
    private val itemA = Item("A", 50)
    private val itemB = Item("B", 75)
    private val itemC = Item("C", 25)
    private val itemD = Item("D", 150)
    private val itemE = Item("E", 200)

    @Nested
    inner class WithMultiplePromotions {
        val unitRules = listOf(itemA, itemB, itemC, itemD, itemE)
            .map(::UnitPriceRule)
        val mealDealRules = listOf(
            setOf(itemA, itemB) to 100,
            setOf(itemC, itemD) to 150
        ).map { (items, price) -> MealDealRule(items, price) }
        val buyBulkRules = listOf(
            itemA to (3 to 130),
            itemB to (2 to 120)
        ).map { (item, pair) -> BuyBulkRule(item, pair.first, pair.second) }
        val buyGetFreeRules = listOf(
            itemD to 2,
            itemE to 2
        ).map { (item, quantity) -> BuyGetFreeRule(item, quantity) }

        val pricingRules = mealDealRules + buyBulkRules + buyGetFreeRules + unitRules

        @Test
        fun `checkout with an empty cart returns zero`() {
            val cart = Cart(mutableMapOf())

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(0, total)
        }

        @Test
        fun `checkout with all rules return value`() {
            val cart = Cart(mutableMapOf(
                itemA to 3,
                itemB to 2,
                itemC to 4,
                itemD to 3,
                itemE to 3
                )
            )

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals((2 * 100) + (3 * 150) + (2 * 200) + 50 + 25, total)
        }

        @Test
        fun `checkout with all rules return value in rule priority order`() {
            val cart = Cart(mutableMapOf(
                itemA to 3,
                itemB to 2,
                itemC to 4,
                itemD to 3,
                itemE to 3
                )
            )

            val pricingRuleInOrder = unitRules + mealDealRules + buyBulkRules + buyGetFreeRules

            val total = checkoutService.checkout(pricingRuleInOrder, cart)

            val expected = (itemA.priceInPence * 3) + (itemB.priceInPence * 2) + (itemC.priceInPence * 4) + (itemD.priceInPence * 3) + (itemE.priceInPence * 3)

            assertEquals(expected, total)
        }
    }

    @Nested
    inner class UnitPricing {
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
}
