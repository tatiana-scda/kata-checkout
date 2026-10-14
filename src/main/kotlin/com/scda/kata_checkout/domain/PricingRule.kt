package com.scda.kata_checkout.domain

sealed interface PricingRule {
    fun apply(cart: Cart): Int
}

data class UnitPriceRule(val item: Item): PricingRule {
    override fun apply(cart: Cart): Int =
        (cart.items.remove(item) ?: 0) * item.priceInPence
}