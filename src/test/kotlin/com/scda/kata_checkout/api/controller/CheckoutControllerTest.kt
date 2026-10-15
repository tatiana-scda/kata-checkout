package com.scda.kata_checkout.api.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutControllerTest(@Autowired val mockMvc: MockMvc) {

    private val items = """
        "items": [
          { "id": "A", "priceInPence": 50 },
          { "id": "B", "priceInPence": 75 }
        ]
    """.trimIndent()

    @Test
    fun `checkout applies the pricing rules to the cart and returns the total`() {
        val request = """
            {
              $items,
              "pricingRules": [
                { "type": "buyBulk", "itemId": "A", "bulkQuantity": 3, "bulkPrice": 130 },
                { "type": "unitPrice", "itemId": "A" },
                { "type": "unitPrice", "itemId": "B" }
              ],
              "cart": {
                "items": [
                  { "itemId": "A", "quantity": 3 },
                  { "itemId": "B", "quantity": 2 }
                ]
              }
            }
        """.trimIndent()

        mockMvc.post("/v1/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isOk() }
            jsonPath("$.totalInPence") { value(130 + 2 * 75) }
        }
    }

    @Test
    fun `checkout with an empty cart returns zero`() {
        val request = """
            {
              $items,
              "pricingRules": [
                { "type": "unitPrice", "itemId": "A" }
              ],
              "cart": { "items": [] }
            }
        """.trimIndent()

        mockMvc.post("/v1/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isOk() }
            jsonPath("$.totalInPence") { value(0) }
        }
    }

    @Test
    fun `checkout with duplicate cart items merges their quantities`() {
        val request = """
            {
              $items,
              "pricingRules": [
                { "type": "unitPrice", "itemId": "A" }
              ],
              "cart": {
                "items": [
                  { "itemId": "A", "quantity": 1 },
                  { "itemId": "A", "quantity": 2 }
                ]
              }
            }
        """.trimIndent()

        mockMvc.post("/v1/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isOk() }
            jsonPath("$.totalInPence") { value(150) }
        }
    }

    @Test
    fun `checkout with an unknown rule type is rejected`() {
        val request = """
            {
              $items,
              "pricingRules": [
                { "type": "unknownRule", "itemId": "A" }
              ],
              "cart": { "items": [] }
            }
        """.trimIndent()

        mockMvc.post("/v1/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `checkout with a non-positive quantity is rejected`() {
        val request = """
            {
              $items,
              "pricingRules": [],
              "cart": {
                "items": [
                  { "itemId": "A", "quantity": 0 }
                ]
              }
            }
        """.trimIndent()

        mockMvc.post("/v1/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `checkout with a rule referencing an unknown item id is rejected`() {
        val request = """
            {
              $items,
              "pricingRules": [
                { "type": "unitPrice", "itemId": "C" }
              ],
              "cart": { "items": [] }
            }
        """.trimIndent()

        mockMvc.post("/v1/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `checkout with a cart entry referencing an unknown item id is rejected`() {
        val request = """
            {
              $items,
              "pricingRules": [],
              "cart": {
                "items": [
                  { "itemId": "C", "quantity": 1 }
                ]
              }
            }
        """.trimIndent()

        mockMvc.post("/v1/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
