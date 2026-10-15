package com.scda.kata_checkout.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UnitPriceRuleTest {

    private val itemA = Item("A", 50)

    @Test
    fun `returns unit price for a single item`() {
        val cart = Cart(mutableMapOf(itemA to 1))

        val total = UnitPriceRule(itemA).apply(cart)

        assertEquals(50, total)
    }

    @Test
    fun `multiplies unit price by quantity`() {
        val cart = Cart(mutableMapOf(itemA to 3))

        val total = UnitPriceRule(itemA).apply(cart)

        assertEquals(150, total)
    }

    @Test
    fun `returns zero when the item is not in the cart`() {
        val cart = Cart(mutableMapOf(Item("B", 75) to 2))

        val total = UnitPriceRule(itemA).apply(cart)

        assertEquals(0, total)
    }

    @Test
    fun `removes the priced item from the cart`() {
        val cart = Cart(mutableMapOf(itemA to 2, Item("B", 75) to 1))

        UnitPriceRule(itemA).apply(cart)

        assertFalse(itemA in cart.items)
    }

    @Test
    fun `leaves other items in the cart untouched`() {
        val itemB = Item("B", 75)
        val cart = Cart(mutableMapOf(itemA to 1, itemB to 1))

        UnitPriceRule(itemA).apply(cart)

        assertEquals(1, cart.items[itemB] )
        assertEquals(null, cart.items[itemA])
    }
}
