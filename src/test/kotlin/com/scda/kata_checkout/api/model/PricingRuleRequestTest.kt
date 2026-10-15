package com.scda.kata_checkout.api.model

import com.scda.kata_checkout.domain.BuyBulkRule
import com.scda.kata_checkout.domain.BuyGetFreeRule
import com.scda.kata_checkout.domain.Item
import com.scda.kata_checkout.domain.MealDealRule
import com.scda.kata_checkout.domain.UnitPriceRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule

class PricingRuleRequestTest {

    private val mapper: JsonMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()

    private val itemA = Item("A", 50)
    private val itemB = Item("B", 75)
    private val itemById = mapOf("A" to itemA, "B" to itemB)

    @Test
    fun `parses unitPrice rule from JSON`() {
        val json = """
            { "type": "unitPrice", "itemId": "A" }
        """.trimIndent()

        val request = mapper.readValue(json, PricingRuleRequest::class.java)

        assertTrue(request is UnitPriceRuleRequest)
        assertEquals(UnitPriceRule(itemA), request.toDomain(itemById))
    }

    @Test
    fun `parses buyGetFree rule from JSON`() {
        val json = """
            { "type": "buyGetFree", "itemId": "A", "buyQuantity": 2 }
        """.trimIndent()

        val request = mapper.readValue(json, PricingRuleRequest::class.java)

        assertTrue(request is BuyGetFreeRuleRequest)
        assertEquals(BuyGetFreeRule(itemA, 2), request.toDomain(itemById))
    }

    @Test
    fun `parses buyBulk rule from JSON`() {
        val json = """
            { "type": "buyBulk", "itemId": "B", "bulkQuantity": 3, "bulkPrice": 200 }
        """.trimIndent()

        val request = mapper.readValue(json, PricingRuleRequest::class.java)

        assertTrue(request is BuyBulkRuleRequest)
        assertEquals(BuyBulkRule(itemB, 3, 200), request.toDomain(itemById))
    }

    @Test
    fun `parses mealDeal rule from JSON`() {
        val json = """
            { "type": "mealDeal", "dealItemIds": ["A", "B"], "dealPrice": 100 }
        """.trimIndent()

        val request = mapper.readValue(json, PricingRuleRequest::class.java)

        assertTrue(request is MealDealRuleRequest)
        assertEquals(MealDealRule(setOf(itemA, itemB), 100), request.toDomain(itemById))
    }

    @Test
    fun `rule referencing an unknown item id is rejected`() {
        val json = """
            { "type": "unitPrice", "itemId": "C" }
        """.trimIndent()

        val request = mapper.readValue(json, PricingRuleRequest::class.java)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            request.toDomain(itemById)
        }
        assertEquals("unknown item id: C", exception.message)
    }

    @Test
    fun `round-trips rules back to JSON`() {
        val request = BuyBulkRuleRequest("B", 3, 200)

        val json = mapper.writeValueAsString(request)
        val parsed = mapper.readValue(json, PricingRuleRequest::class.java)

        assertEquals(request, parsed)
    }
}
