package com.scda.kata_checkout.api.model

import com.scda.kata_checkout.domain.Cart
import com.scda.kata_checkout.domain.Item

data class CartItemRequest(val itemId: String, val quantity: Int) {
    init {
        require(quantity > 0) { "quantity must be positive" }
    }
}

data class CartRequest(val items: List<CartItemRequest>) {
    fun toDomain(itemById: Map<String, Item>): Cart {
        val itemsByItem = mutableMapOf<Item, Int>()
        items.forEach { cartItem ->
            itemsByItem.merge(itemById.resolve(cartItem.itemId), cartItem.quantity, Int::plus)
        }
        return Cart(itemsByItem)
    }
}

data class CheckoutRequest(
    val items: List<ItemRequest>,
    val pricingRules: List<PricingRuleRequest>,
    val cart: CartRequest,
) {
    fun itemById(): Map<String, Item> = items.associate { it.id to it.toDomain() }
}

data class CheckoutResponse(val totalInPence: Int)
