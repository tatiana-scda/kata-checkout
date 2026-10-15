package com.scda.kata_checkout.domain

sealed interface PricingRule {
    fun apply(cart: Cart): Int
}

data class UnitPriceRule(val item: Item): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = getItemTotalOrZero(item, cart)
        cart.items.remove(item)

        return quantityInCart * item.priceInPence
    }
}

data class BuyGetFreeRule(val item: Item, val buyQuantity: Int): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = getItemTotalOrZero(item, cart)
   
        if (quantityInCart > buyQuantity) {
            val packsOfBuyQuantity = buyQuantity + 1
            val outsidePromotionPack = quantityInCart % packsOfBuyQuantity
            val paidItems = ((quantityInCart - outsidePromotionPack) / packsOfBuyQuantity) * buyQuantity
            
            cart.items[item] = outsidePromotionPack

            return (paidItems * item.priceInPence)
        } else {
            return Integer.valueOf(0)
        }
    }
}

data class BuyBulkRule(val item: Item, val bulkQuantity: Int, val bulkPrice: Int): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = getItemTotalOrZero(item, cart)

        if (quantityInCart >= bulkQuantity) {
            val outsideBulkPack = quantityInCart % bulkQuantity
            val bulkPacks = (quantityInCart - outsideBulkPack) / bulkQuantity

            cart.items[item] = outsideBulkPack
            
            return bulkPacks * bulkPrice
        } else {
            return Integer.valueOf(0)
        }
    }
}

data class MealDealRule(val dealItems: Set<Item>, val dealPrice: Int): PricingRule {
    override fun apply(cart: Cart): Int {

        if (cart.items.keys.containsAll(dealItems)) {
            val totalDeals = dealItems.minOf { cart.items[it] ?: 0 }

            dealItems.forEach { i -> cart.items[i] = cart.items[i]?.minus(totalDeals) ?: 0 }

            return totalDeals * dealPrice
        } else {
            return Integer.valueOf(0)
        }
    }
}

private fun getItemTotalOrZero(item: Item, cart: Cart): Int = cart.items[item] ?: 0
