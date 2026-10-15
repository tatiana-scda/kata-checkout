package com.scda.kata_checkout.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class BuyBulkRuleTest {

    private val itemA = Item("A", 50)

    @Test
    fun `no price nor removal for a single item`() {
        val cart = Cart(mutableMapOf(itemA to 1))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(0, total)
        assertEquals(1, cart.items[itemA])
    }
    
    @Test
    fun `returns price for one bulk pack`() {
        val cart = Cart(mutableMapOf(itemA to 2))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(70, total)
        assertEquals(0, cart.items[itemA])

    }
    
    @Test
    fun `returns price for multiple bulk packs`() {
        val cart = Cart(mutableMapOf(itemA to 6))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(70 * 3, total)
        assertEquals(0, cart.items[itemA])

    }
    
    @Test
    fun `returns price for multiple bulk packs without removing item outside the promotion`() {
        val cart = Cart(mutableMapOf(itemA to 3))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(70, total)
        assertEquals(1, cart.items[itemA])

    }
}
