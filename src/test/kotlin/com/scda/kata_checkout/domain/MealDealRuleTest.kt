package com.scda.kata_checkout.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class MealDealRuleTest {

    private val itemA = Item("A", 50)
    private val itemB = Item("B", 75)

    @Test
    fun `returns unit price for a unique deal items`() {
        val cart = Cart(mutableMapOf(itemA to 1, itemB to 1))

        val total = MealDealRule(setOf(itemA, itemB), 100).apply(cart)

        assertEquals(100, total)
    }

    @Test
    fun `returns unit price for a multiple deal items`() {
        val cart = Cart(mutableMapOf(itemA to 3, itemB to 3))

        val total = MealDealRule(setOf(itemA, itemB), 100).apply(cart)

        assertEquals(300, total)
    }

    @Test
    fun `returns unit price for odd deal items`() {
        val cart = Cart(mutableMapOf(itemA to 3, itemB to 2))

        val total = MealDealRule(setOf(itemA, itemB), 100).apply(cart)

        assertEquals(250, total)
    }

    @Test
    fun `returns unit price for multiple odd deal items`() {
        val cart = Cart(mutableMapOf(itemA to 3, itemB to 5))

        val total = MealDealRule(setOf(itemA, itemB), 100).apply(cart)

        assertEquals(450, total)
    }
}
