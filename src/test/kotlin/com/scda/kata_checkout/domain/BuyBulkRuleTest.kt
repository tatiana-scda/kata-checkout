package com.scda.kata_checkout.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class BuyBulkRuleTest {

    private val itemA = Item("A", 50)

    @Test
    fun `returns unit price for a single item`() {
        val cart = Cart(mutableMapOf(itemA to 1))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(50, total)
    }
    
    @Test
    fun `returns price for one bulk pack`() {
        val cart = Cart(mutableMapOf(itemA to 2))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(70, total)
    }
    
    @Test
    fun `returns price for multiple bulk packs`() {
        val cart = Cart(mutableMapOf(itemA to 6))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(70 * 3, total)
    }
    
    @Test
    fun `returns price for multiple bulk packs plus item outside the promotion`() {
        val cart = Cart(mutableMapOf(itemA to 3))

        val total = BuyBulkRule(itemA, 2, 70).apply(cart)

        assertEquals(70 + 50, total)
    }
}
