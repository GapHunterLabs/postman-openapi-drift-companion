package dev.gaphunter.postmanopenapidriftcompanion.parse

/**
 * Compares a Postman endpoint's path segments against the set of
 * OpenAPI path templates, segment by segment, treating any
 * placeholder-shaped segment as a wildcard on both sides -- OpenAPI
 * uses `{id}`, Postman uses `:id` (path variable) or `{{id}}`
 * (collection/environment variable interpolated into the URL). A
 * literal segment must match exactly (case-sensitive, real REST paths
 * are case-sensitive).
 */
object PathMatcher {

    private val PLACEHOLDER = Regex("""^(\{[^}]*}|\{\{[^}]*}}|:.+)$""")

    fun existsInSpec(postmanSegments: List<String>, openApiPaths: Set<String>): Boolean {
        if (openApiPaths.isEmpty()) return true // no spec found/parsed -- honestly "can't evaluate", never a false positive
        return openApiPaths.any { specPath -> segmentsMatch(postmanSegments, specPath.trim('/').split("/")) }
    }

    private fun segmentsMatch(postmanSegments: List<String>, specSegments: List<String>): Boolean {
        if (postmanSegments.size != specSegments.size) return false
        return postmanSegments.indices.all { i ->
            val postmanSeg = postmanSegments[i]
            val specSeg = specSegments[i]
            isPlaceholder(postmanSeg) || isPlaceholder(specSeg) || postmanSeg == specSeg
        }
    }

    private fun isPlaceholder(segment: String): Boolean = PLACEHOLDER.matches(segment)
}
