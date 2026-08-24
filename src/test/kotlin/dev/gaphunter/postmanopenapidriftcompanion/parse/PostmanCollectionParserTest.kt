package dev.gaphunter.postmanopenapidriftcompanion.parse

import com.intellij.json.psi.JsonFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class PostmanCollectionParserTest : BasePlatformTestCase() {

    fun `test finds endpoint with path segment array`() {
        val file = myFixture.configureByText(
            "demo.postman_collection.json",
            """
            {
                "info": { "name": "Demo" },
                "item": [
                    {
                        "name": "Get user",
                        "request": {
                            "method": "GET",
                            "url": {
                                "raw": "{{baseUrl}}/users/:id",
                                "path": ["users", ":id"]
                            }
                        }
                    }
                ]
            }
            """.trimIndent(),
        )
        val endpoints = PostmanCollectionParser.findEndpoints(file as JsonFile)
        assertEquals(1, endpoints.size)
        assertEquals("GET", endpoints[0].method)
        assertEquals(listOf("users", ":id"), endpoints[0].pathSegments)
    }

    fun `test finds endpoints nested inside a folder item`() {
        val file = myFixture.configureByText(
            "demo.postman_collection.json",
            """
            {
                "info": { "name": "Demo" },
                "item": [
                    {
                        "name": "Users folder",
                        "item": [
                            {
                                "name": "List users",
                                "request": {
                                    "method": "GET",
                                    "url": { "raw": "{{baseUrl}}/users", "path": ["users"] }
                                }
                            }
                        ]
                    }
                ]
            }
            """.trimIndent(),
        )
        val endpoints = PostmanCollectionParser.findEndpoints(file as JsonFile)
        assertEquals(1, endpoints.size)
        assertEquals(listOf("users"), endpoints[0].pathSegments)
    }

    fun `test falls back to raw string url when no path array present`() {
        val file = myFixture.configureByText(
            "demo.postman_collection.json",
            """
            {
                "info": { "name": "Demo" },
                "item": [
                    {
                        "name": "Get order",
                        "request": {
                            "method": "GET",
                            "url": "{{baseUrl}}/orders/123"
                        }
                    }
                ]
            }
            """.trimIndent(),
        )
        val endpoints = PostmanCollectionParser.findEndpoints(file as JsonFile)
        assertEquals(1, endpoints.size)
        assertEquals(listOf("orders", "123"), endpoints[0].pathSegments)
    }
}
