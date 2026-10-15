package com.scda.kata_checkout.domain

sealed interface PricingRule {
    fun apply(cart: Cart): Int
}

data class UnitPriceRule(val item: Item): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = getItemTotal(item, cart)
        cart.items.remove(item)

        return quantityInCart * item.priceInPence
    }
}

data class BuyGetFreeRule(val item: Item, val buyQuantity: Int): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = getItemTotal(item, cart)
   
        if (quantityInCart > buyQuantity) {
            val packsOfBuyQuantity = buyQuantity + 1
            val outsidePromotionPack = quantityInCart % packsOfBuyQuantity
            val paidItems = ((quantityInCart - outsidePromotionPack) / packsOfBuyQuantity) * buyQuantity
            
            cart.items.remove(item)
            
            return (paidItems * item.priceInPence) + (outsidePromotionPack * item.priceInPence)
        } else {
            return UnitPriceRule(item).apply(cart)
        }
    }
}

data class BuyBulkRule(val item: Item, val bulkQuantity: Int, val bulkPrice: Int): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = getItemTotal(item, cart)

        if (quantityInCart >= bulkQuantity) {
            val outsideBulkPack = quantityInCart % bulkQuantity
            val bulkPacks = (quantityInCart - outsideBulkPack) / bulkQuantity
            cart.items.remove(item)
            
            return (bulkPacks * bulkPrice) + (outsideBulkPack * item.priceInPence)
        } else {
            return UnitPriceRule(item).apply(cart)
        }
    }
}

private fun getItemTotal(item: Item, cart: Cart): Int = cart.items[item] ?: 0
