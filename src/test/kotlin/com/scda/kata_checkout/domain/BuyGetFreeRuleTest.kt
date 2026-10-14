package com.scda.kata_checkout.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class BuyGetFreeRuleTest {

    private val itemA = Item("A", 50)

    @Test
    fun `returns unit price for a single item`() {
        val cart = Cart(mutableMapOf(itemA to 1))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(50, total)
    }
    
    @Test
    fun `returns price for multiple items without free item`() {
        val cart = Cart(mutableMapOf(itemA to 2))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(100, total)
    }
    
    @Test
    fun `returns price for multiple items but with free item`() {
        val cart = Cart(mutableMapOf(itemA to 3))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(100, total)
    }
    
    @Test
    fun `returns price for multiple items plus item outside the promotion`() {
        val cart = Cart(mutableMapOf(itemA to 7))

        val total = BuyGetFreeRule(itemA, 2).apply(cart)

        assertEquals(250, total)
    }
}
