package com.scda.kata_checkout.api.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.scda.kata_checkout.domain.BuyBulkRule
import com.scda.kata_checkout.domain.BuyGetFreeRule
import com.scda.kata_checkout.domain.Item
import com.scda.kata_checkout.domain.MealDealRule
import com.scda.kata_checkout.domain.PricingRule
import com.scda.kata_checkout.domain.UnitPriceRule

data class ItemRequest(val id: String, val priceInPence: Int) {
    fun toDomain(): Item = Item(id, priceInPence)
}

internal fun Map<String, Item>.resolve(itemId: String): Item =
    this[itemId] ?: throw IllegalArgumentException("unknown item id: $itemId")

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = UnitPriceRuleRequest::class, name = "unitPrice"),
    JsonSubTypes.Type(value = BuyGetFreeRuleRequest::class, name = "buyGetFree"),
    JsonSubTypes.Type(value = BuyBulkRuleRequest::class, name = "buyBulk"),
    JsonSubTypes.Type(value = MealDealRuleRequest::class, name = "mealDeal"),
)
sealed interface PricingRuleRequest {
    fun toDomain(itemById: Map<String, Item>): PricingRule
}

data class UnitPriceRuleRequest(val itemId: String) : PricingRuleRequest {
    override fun toDomain(itemById: Map<String, Item>): PricingRule = UnitPriceRule(itemById.resolve(itemId))
}

data class BuyGetFreeRuleRequest(val itemId: String, val buyQuantity: Int) : PricingRuleRequest {
    override fun toDomain(itemById: Map<String, Item>): PricingRule = BuyGetFreeRule(itemById.resolve(itemId), buyQuantity)
}

data class BuyBulkRuleRequest(val itemId: String, val bulkQuantity: Int, val bulkPrice: Int) : PricingRuleRequest {
    override fun toDomain(itemById: Map<String, Item>): PricingRule = BuyBulkRule(itemById.resolve(itemId), bulkQuantity, bulkPrice)
}

data class MealDealRuleRequest(val dealItemIds: Set<String>, val dealPrice: Int) : PricingRuleRequest {
    override fun toDomain(itemById: Map<String, Item>): PricingRule =
        MealDealRule(dealItemIds.map { itemById.resolve(it) }.toSet(), dealPrice)
}
