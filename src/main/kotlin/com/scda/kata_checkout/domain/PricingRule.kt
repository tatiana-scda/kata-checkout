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
            
            cart.items.remove(item)
            
            return (paidItems * item.priceInPence) + (outsidePromotionPack * item.priceInPence)
        } else {
            return UnitPriceRule(item).apply(cart)
        }
    }
}

data class BuyBulkRule(val item: Item, val bulkQuantity: Int, val bulkPrice: Int): PricingRule {
    override fun apply(cart: Cart): Int {
        val quantityInCart = getItemTotalOrZero(item, cart)

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

data class MealDealRule(val dealItems: Set<Item>, val dealPrice: Int): PricingRule {
    override fun apply(cart: Cart): Int {

        if (cart.items.keys.containsAll(dealItems)) {
           val cartWithDealOnly = mutableMapOf<Item, Int>()

            cart.items.forEach { (item, units) ->
                if (dealItems.contains(item)) {
                    cartWithDealOnly[item] = units
                }
            }

            val totalDeals: Int = cartWithDealOnly.values.minOrNull() ?: 0

            dealItems.forEach { dealItem -> cart.items.computeIfPresent(dealItem) { _, current ->
                (current - totalDeals).coerceAtLeast(0)
            } }

            val outsideDealValue = cart.items.entries.sumOf { (item, units) -> item.priceInPence * units }

            return (totalDeals * dealPrice) + (outsideDealValue)
        } else {
            return dealItems.sumOf { UnitPriceRule(it).apply(cart) }
        }
    }
}

private fun getItemTotalOrZero(item: Item, cart: Cart): Int = cart.items[item] ?: 0
