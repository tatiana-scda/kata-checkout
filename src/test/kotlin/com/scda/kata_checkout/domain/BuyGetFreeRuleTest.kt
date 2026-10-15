package com.scda.kata_checkout.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class BuyGetFreeRuleTest {

    private val itemA = Item("A", 50)

    @Test
    fun `no price for a single item`() {
        val cart = Cart(mutableMapOf(itemA to 1))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(0, total)
        assertEquals(1, cart.items[itemA])
    }
    
    @Test
    fun `no price for multiple items if no free item`() {
        val cart = Cart(mutableMapOf(itemA to 2))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(0, total)
        assertEquals(2, cart.items[itemA])
    }
    
    @Test
    fun `returns price for multiple items but with free item`() {
        val cart = Cart(mutableMapOf(itemA to 3))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(100, total)
        assertEquals(0, cart.items[itemA])

    }
    
    @Test
    fun `returns price for multiple items without item outside the promotion`() {
        val cart = Cart(mutableMapOf(itemA to 7))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(2 * 100, total)
        assertEquals(1, cart.items[itemA])
    }
}
