package com.scda.kata_checkout.api

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule

@SpringBootTest
@AutoConfigureMockMvc
class PostmanCollectionTest(@Autowired val mockMvc: MockMvc) {

    private val mapper: JsonMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()

    private val collection: Map<String, Any?> = mapper.readValue(
        java.io.File("docs/kata-checkout.postman_collection.json").readText(),
        Map::class.java,
    ) as Map<String, Any?>

    @Test
    fun `every postman request hits the API and matches its expected status and total`() {
        val requests = (collection["item"] as List<Map<String, Any?>>)
            .flatMap { folder -> folder["item"] as List<Map<String, Any?>> }

        assertTrue(requests.isNotEmpty(), "no requests found in the collection")

        requests.forEach { request ->
            val name = request["name"] as String
            val rawRequest = request["request"] as Map<String, Any?>
            val body = (rawRequest["body"] as Map<String, Any?>)["raw"] as String
            val (expectedStatus, expectedTotal) = extractAssertions(request)

            mockMvc.post("/v1/checkout") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                if (expectedStatus == 200) status { isOk() } else status { isBadRequest() }
                expectedTotal?.let { jsonPath("$.totalInPence") { value(it) } }
            }

            println("$name -> $expectedStatus, total=$expectedTotal")
        }
    }

    private fun extractAssertions(request: Map<String, Any?>): Pair<Int, Int?> {
        val script = (request["event"] as List<Map<String, Any?>>)
            .first { it["listen"] == "test" }
            .let { (it["script"] as Map<String, Any?>)["exec"] as List<String> }
            .joinToString("\n")

        val expectedStatus = Regex("""to\.have\.status\((\d+)\)""")
            .find(script)!!
            .groupValues[1]
            .toInt()

        val expectedTotal = Regex("""to\.eql\((\d+)\)""")
            .find(script)
            ?.groupValues
            ?.get(1)
            ?.toInt()

        return expectedStatus to expectedTotal
    }
}
