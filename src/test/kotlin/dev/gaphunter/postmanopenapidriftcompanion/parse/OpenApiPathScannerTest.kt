package dev.gaphunter.postmanopenapidriftcompanion.parse

import org.junit.Assert.assertEquals
import org.junit.Test

class OpenApiPathScannerTest {

    @Test
    fun `scans yaml paths block`() {
        val yaml = """
            openapi: 3.0.0
            info:
              title: Demo
            paths:
              /users:
                get:
                  summary: list users
              /users/{id}:
                get:
                  summary: get user
            components:
              schemas: {}
        """.trimIndent()
        assertEquals(setOf("/users", "/users/{id}"), OpenApiPathScanner.scan(yaml))
    }

    @Test
    fun `scans json paths block`() {
        val json = """
            {
              "openapi": "3.0.0",
              "paths": {
                "/orders": { "get": {} },
                "/orders/{id}": { "get": {} }
              }
            }
        """.trimIndent()
        assertEquals(setOf("/orders", "/orders/{id}"), OpenApiPathScanner.scan(json))
    }

    @Test
    fun `returns empty set when no paths block present`() {
        assertEquals(emptySet<String>(), OpenApiPathScanner.scan("openapi: 3.0.0\ninfo:\n  title: Demo\n"))
    }
}
