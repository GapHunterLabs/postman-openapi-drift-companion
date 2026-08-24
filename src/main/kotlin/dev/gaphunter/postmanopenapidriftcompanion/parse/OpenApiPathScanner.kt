package dev.gaphunter.postmanopenapidriftcompanion.parse

/**
 * Plain-text scan for a `paths:` (YAML) or `"paths"` (JSON) OpenAPI/
 * Swagger document -- same "indentation/brace scanning, no grammar PSI"
 * principle already proven in `k8s-resource-limit-companion`'s
 * `K8sManifestScanner`, since the spec can be either YAML or JSON and
 * this plugin takes no YAML PSI dependency.
 *
 * Returns the set of path templates declared under `paths:`, e.g.
 * `/users/{id}/orders`. v0.1 doesn't resolve `$ref`, so a spec that
 * only declares paths via external references produces an honestly
 * empty set (never a false "path not found" for those).
 */
object OpenApiPathScanner {

    private val YAML_PATH_KEY = Regex("""^\s{2,}(/\S*):\s*$""")
    private val JSON_PATH_KEY = Regex(""""(/[^"]*)"\s*:\s*\{""")
    private val PATHS_ROOT_YAML = Regex("""^paths:\s*$""")
    private val PATHS_ROOT_JSON = Regex(""""paths"\s*:\s*\{""")

    fun scan(text: String): Set<String> {
        val lines = text.lines()
        val isJson = text.trimStart().startsWith("{")
        val paths = mutableSetOf<String>()

        if (isJson) {
            var insidePaths = false
            var depth = 0
            for (line in lines) {
                if (!insidePaths) {
                    if (PATHS_ROOT_JSON.containsMatchIn(line)) {
                        insidePaths = true
                        depth = 1
                    }
                    continue
                }
                depth += line.count { it == '{' } - line.count { it == '}' }
                JSON_PATH_KEY.find(line)?.let { paths += it.groupValues[1] }
                if (depth <= 0) insidePaths = false
            }
        } else {
            var insidePaths = false
            for (line in lines) {
                if (!insidePaths) {
                    if (PATHS_ROOT_YAML.matches(line)) insidePaths = true
                    continue
                }
                if (line.isNotBlank() && !line.startsWith(" ") && !line.startsWith("\t")) {
                    insidePaths = false
                    continue
                }
                YAML_PATH_KEY.find(line)?.let { paths += it.groupValues[1] }
            }
        }
        return paths
    }
}
