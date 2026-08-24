package dev.gaphunter.postmanopenapidriftcompanion.parse

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PathMatcherTest {

    @Test
    fun `exact literal path matches`() {
        assertTrue(PathMatcher.existsInSpec(listOf("users"), setOf("/users")))
    }

    @Test
    fun `postman colon placeholder matches openapi brace placeholder`() {
        assertTrue(PathMatcher.existsInSpec(listOf("users", ":id"), setOf("/users/{id}")))
    }

    @Test
    fun `postman double-brace variable matches openapi brace placeholder`() {
        assertTrue(PathMatcher.existsInSpec(listOf("users", "{{userId}}"), setOf("/users/{id}")))
    }

    @Test
    fun `path not present in spec does not match`() {
        assertFalse(PathMatcher.existsInSpec(listOf("legacy-users"), setOf("/users")))
    }

    @Test
    fun `different segment count does not match`() {
        assertFalse(PathMatcher.existsInSpec(listOf("users", "1", "orders"), setOf("/users/{id}")))
    }

    @Test
    fun `empty spec paths means can't evaluate, never a false positive`() {
        assertTrue(PathMatcher.existsInSpec(listOf("anything"), emptySet()))
    }
}
