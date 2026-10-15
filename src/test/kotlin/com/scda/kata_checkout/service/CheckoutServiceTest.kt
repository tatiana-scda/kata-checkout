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
            assertEquals(0, cart.items.size)
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
            assertEquals(0, cart.items.size)
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
            assertEquals(0, cart.items.size)
        }
    }

    @Nested
    inner class UnitPricing {
        private val pricingRules = listOf(itemA, itemB, itemC, itemD, itemE)
            .map(::UnitPriceRule)

        @Test
        fun `checkout with a single item returns its unit price`() {
            val cart = Cart(mutableMapOf(itemA to 1))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(50, total)
            assertEquals(null, cart.items[itemA])
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
            assertEquals(null, cart.items[itemA])
            assertEquals(null, cart.items[itemB])
            assertEquals(null, cart.items[itemC])
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
            assertEquals(null, cart.items[itemD])
            assertEquals(null, cart.items[itemE])
        }
    }

    @Nested
    inner class MealDealPricing {
        private val pricingRules = listOf(
            MealDealRule(setOf(itemA, itemB), 100),
            MealDealRule(setOf(itemC, itemD), 150)
        )

        @Test
        fun `checkout with one of each deal item returns the deal price`() {
            val cart = Cart(mutableMapOf(itemA to 1, itemB to 1))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(100, total)
        }

        @Test
        fun `checkout with multiples of deal items returns the deal price per complete deal`() {
            val cart = Cart(mutableMapOf(itemA to 3, itemB to 3, itemC to 1, itemD to 1))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(3 * 100 + 150, total)
        }

        @Test
        fun `checkout with incomplete deals prices the complete deals and leaves the leftovers`() {
            val cart = Cart(mutableMapOf(itemA to 3, itemB to 2))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(2 * 100, total)
            assertEquals(1, cart.items[itemA])
        }

        @Test
        fun `checkout with a missing deal item leaves the items to other rules`() {
            val cart = Cart(mutableMapOf(itemA to 2))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(0, total)
            assertEquals(2, cart.items[itemA])
        }
    }

    @Nested
    inner class BuyBulkPricing {
        private val pricingRules = listOf(
            BuyBulkRule(itemA, 3, 130),
            BuyBulkRule(itemB, 2, 120)
        )

        @Test
        fun `checkout with the bulk quantity returns the bulk price`() {
            val cart = Cart(mutableMapOf(itemA to 3, itemB to 2))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(130 + 120, total)
        }

        @Test
        fun `checkout with bulk packs plus a leftover prices the packs and leaves the leftover`() {
            val cart = Cart(mutableMapOf(itemA to 5))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(130, total)
            assertEquals(2, cart.items[itemA])
        }

        @Test
        fun `checkout below the bulk quantity leaves the items to other rules`() {
            val cart = Cart(mutableMapOf(itemA to 2))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(0, total)
            assertEquals(2, cart.items[itemA])
        }
    }

    @Nested
    inner class BuyGetFreePricing {
        private val pricingRules = listOf(
            BuyGetFreeRule(itemD, 2),
            BuyGetFreeRule(itemE, 2)
        )

        @Test
        fun `checkout with a full promotion pack charges only the paid items`() {
            val cart = Cart(mutableMapOf(itemD to 3, itemE to 3))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(2 * 150 + 2 * 200, total)
        }

        @Test
        fun `checkout with items outside the promotion pack leaves them to other rules`() {
            val cart = Cart(mutableMapOf(itemD to 4))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(2 * 150, total)
            assertEquals(1, cart.items[itemD])
        }

        @Test
        fun `checkout without the free item quantity leaves the items to other rules`() {
            val cart = Cart(mutableMapOf(itemD to 2))

            val total = checkoutService.checkout(pricingRules, cart)

            assertEquals(0, total)
            assertEquals(2, cart.items[itemD])
        }
    }
}
