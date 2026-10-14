package com.scda.kata_checkout.domain

sealed interface PricingRule {
    fun apply(cart: Cart): Int
}

data class UnitPriceRule(val item: Item): PricingRule {
    override fun apply(cart: Cart): Int =
        (cart.items.remove(item) ?: 0) * item.priceInPence
}

data class BuyGetFreeRule(val item: Item, val buyQuantity: Int): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = cart.items.remove(item) ?: 0
        if (quantityInCart > buyQuantity) {
            val outsidePromo = quantityInCart % buyQuantity
            val paidItems = (quantityInCart - outsidePromo) / buyQuantity
            return paidItems * item.priceInPence + outsidePromo * item.priceInPence
        } else {
            return quantityInCart * item.priceInPence
        }
    }
}